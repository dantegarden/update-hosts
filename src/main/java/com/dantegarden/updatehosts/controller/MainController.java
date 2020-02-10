package com.dantegarden.updatehosts.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.dantegarden.updatehosts.UpdateHostsApplication;
import com.dantegarden.updatehosts.dao.DomainRepository;
import com.dantegarden.updatehosts.entity.Domain;
import com.dantegarden.updatehosts.service.GitSource;
import com.dantegarden.updatehosts.service.HostsService;
import com.dantegarden.updatehosts.service.ProgressHolder;
import com.dantegarden.updatehosts.utils.GitUtils;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;


/**
 * @description:
 * @author: lij
 * @create: 2020-02-07 22:15
 */
@Component
@Slf4j
public class MainController {

    @Autowired
    private GitSource gitSource;
    @Autowired
    private HostsService hostsService;
    @Autowired
    private DomainRepository domainDao;
    @Autowired
    private ProgressHolder progressHolder;

    private static final String GOOGLE_HOST_GIT = "https://github.com/googlehosts/hosts.git";
    private static final String SYS_HOSTS_PATH = "C:\\Windows\\System32\\drivers\\etc\\hosts";
    private static final String TMP_HOSTS_DIR = "./target/tmp";

    /**列表组件*/
    public TableView<Domain> lvDomain;
    /**地址栏*/
    public TextField tfGitDir;
    /**新加域名*/
    public TextField newDomain;
    /**进度条*/
    public ProgressBar pb;
    
    private Stage dialogStage;
    private ProgressIndicator progressIndicator;

    /**窗口出现时自动执行*/
    public void initialize(){
        log.info("initialize");
        GitUtils.init(gitSource); //初始化git用户名密码
        progressHolder.init(pb);
        refreshDomainListView();
    }

    public void refreshDomainListView(){
        log.info("refreshDomainList");
        List<Domain> allDomain = domainDao.findAll();
        lvDomain.getItems().setAll(allDomain); //不用清空，直接重设
    }

    /**添加记录*/
    public void addDomain(ActionEvent actionEvent) {
        String newDomainHost = newDomain.getText();
        if(StrUtil.isBlank(newDomainHost)){
            return;
        }

        Domain domain = new Domain();
        domain.setHost(newDomainHost);
        if(CollectionUtil.isNotEmpty(domainDao.findAll(Example.of(domain)))){
            return; //已经存在了
        }
        //补齐信息，入库
        domain.setCreateDate(new Date()).setUpdateDate(new Date());
        domainDao.save(domain);
        //清空输入框，并刷新列表
        newDomain.setText("");
        refreshDomainListView();
    }

    /**新增记录*/
    public void deleteDomain(ActionEvent actionEvent) {
        //首先获取要删除的记录的id，删除，然后刷新列表
        Domain selectedItem = lvDomain.getSelectionModel().getSelectedItem();
        if(selectedItem == null){
            return;
        }

        domainDao.deleteById(selectedItem.getId());
        refreshDomainListView();
    }

    /**更新hosts*/
    public void updateHosts(ActionEvent actionEvent) throws IOException {
        String gitDirPath = tfGitDir.getText();
        if(StrUtil.isBlank(gitDirPath)){
            gitDirPath = "./target" + File.separator + "repo" + File.separator + "hosts";
        }

        boolean existGitRepo = false;
        File gitDirectory = new File(gitDirPath);
        if(gitDirectory.exists()){
            if(!gitDirectory.isDirectory()){
                gitDirectory.delete();
            }else{
                for (File file : gitDirectory.listFiles()) {
                    existGitRepo = file.getName().equals(".git");
                    if(existGitRepo) break;
                }
                if(!existGitRepo) gitDirectory.delete();
            }
        }

        //查询所有额外域名
        List<Domain> domainList = domainDao.findAll();
        progressHolder.start(domainList.size() + 3);

        if(existGitRepo){
            log.info("start updating google hosts repo");
            GitUtils.gitPull(gitDirectory);
        }else{
            log.info("google hosts repo do not exist");
            log.info("start cloning google hosts reop");
            GitUtils.gitClone(GOOGLE_HOST_GIT, gitDirectory);
        }
        progressHolder.go();

        String googleHostsPath = gitDirPath + File.separator + "hosts-files" + File.separator + "hosts";
        File googleHosts = new File(googleHostsPath);

        if(googleHosts != null){
            log.info("update hosts : {}", googleHosts.getAbsolutePath());
            File sysHosts = new File(SYS_HOSTS_PATH);
            //比对系统hosts和google hosts的修改事件
            boolean isNewer = FileUtil.newerThan(googleHosts, sysHosts);
            if(true){ //FIXME isNewer
                //TODO copy googleHosts 临时文件tmpHosts
                File tmpHostsDir = new File(TMP_HOSTS_DIR);
                if(!tmpHostsDir.exists() || !tmpHostsDir.isDirectory()){
                    tmpHostsDir.mkdir();
                }
                File tmpHosts = FileUtil.createTempFile(tmpHostsDir, true);
                FileUtil.copy(googleHosts, tmpHosts, true);
                //TODO 爬取ip 并保存
                hostsService.doCrawler(domainList);
                //TODO 添加到临时文件tmpHosts
                domainList.forEach(domain -> {
                    log.info("添加{}到tmpHosts", domain.getHost());
                    if(StrUtil.isNotBlank(domain.getIp())){
                        String[] ipArr = domain.getIp().split(",");
                        for (int i = 0; i < ipArr.length; i++) {
                            String hostLine = StrUtil.format("{}\t{}",ipArr[i], domain.getHost());
                            FileUtil.appendLines(Collections.singletonList(hostLine), tmpHosts, "UTF-8");
                        }
                    }
                });
                progressHolder.go();
                this.refreshDomainListView();

                //TODO 临时文件替换系统文件
                log.info("start to copy host files. from {} to {} ", tmpHosts.getAbsolutePath(), sysHosts.getAbsolutePath());
                FileUtil.copy(tmpHosts, sysHosts, true);
                log.info("copying hosts files finished ");
                progressHolder.finish();
                alert("更新完毕！");
            }else{
                log.info("system hosts is newest, no necessary to update");
                progressHolder.finish();
                alert("您的hosts文件已是最新，无需更新！");
            }
        }else {
            log.info("google hosts do not exists");
            progressHolder.finish();
        }

    }

    /**选择GirDir*/
    public void selectGitDir(ActionEvent actionEvent) {
        DirectoryChooserBuilder builder = DirectoryChooserBuilder.create();
        builder.title("选择google hosts存放位置");
        String cwd = System.getProperty("user.dir");
        File file = new File(cwd);
        builder.initialDirectory(file);
        DirectoryChooser chooser = builder.build();
        File chosenDir = chooser.showDialog(UpdateHostsApplication.primaryStage);
        if(chosenDir != null){
            log.info(chosenDir.getAbsolutePath());
            tfGitDir.setText(chosenDir.getAbsolutePath());
        }
    }

    public void alert(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
