package com.dantegarden.updatehosts.utils;

import com.dantegarden.updatehosts.service.GitSource;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;

/**
 * @description:
 * @author: lij
 * @create: 2020-02-08 14:42
 */
@Slf4j
public class GitUtils {

    private static String username = "";
    private static String password = "";

    public static void init(GitSource gitSource){
        username = gitSource.getUsername();
        password = gitSource.getPassword();
    }

    public static void gitClone(String remoteUrl, File repoDir) {
        try {
            CloneCommand cloneCommand = Git.cloneRepository()
                    .setURI(remoteUrl)
                    .setDirectory(repoDir);
            if (remoteUrl.contains("http") || remoteUrl.contains("https")) {
                UsernamePasswordCredentialsProvider user = new UsernamePasswordCredentialsProvider(username, password);
                cloneCommand.setCredentialsProvider(user);
            }
            Git repo = cloneCommand.call();
            log.info("Cloning from " + remoteUrl + " to " + repo.getRepository());
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    public static void gitPull(File repoDir) {
        File RepoGitDir = new File(repoDir.getAbsolutePath() + "/.git");
        if (!RepoGitDir.exists()) {
            log.info("Error! Not Exists : " + RepoGitDir.getAbsolutePath());
        } else {
            Repository repo = null;
            try {
                repo = new FileRepository(RepoGitDir.getAbsolutePath());
                Git git = new Git(repo);
                PullCommand pullCmd = git.pull();
                pullCmd.call();

                log.info("Pulled from remote repository to local repository at " + repo.getDirectory());
            } catch (Exception e) {
                log.info(e.getMessage() + " : " + RepoGitDir.getAbsolutePath());
            } finally {
                if (repo != null) {
                    repo.close();
                }
            }
        }
    }

    public static void gitShowStatus(File repoDir) {
        File RepoGitDir = new File(repoDir.getAbsolutePath() + "/.git");
        if (!RepoGitDir.exists()) {
            log.info("Error! Not Exists : " + RepoGitDir.getAbsolutePath());
        } else {
            Repository repo = null;
            try {
                repo = new FileRepository(RepoGitDir.getAbsolutePath());
                Git    git    = new Git(repo);
                Status status = git.status().call();
                log.info("Git Change: " + status.getChanged());
                log.info("Git Modified: " + status.getModified());
                log.info("Git UncommittedChanges: " + status.getUncommittedChanges());
                log.info("Git Untracked: " + status.getUntracked());
            } catch (Exception e) {
                log.info(e.getMessage() + " : " + repoDir.getAbsolutePath());
            } finally {
                if (repo != null) {
                    repo.close();
                }
            }
        }
    }
}
