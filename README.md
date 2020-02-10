# update-hosts
一个自动更新hosts的小工具。

基本原理是
1. 自动拉取google-hosts仓库代码，获得最新的hosts文件。
2. 增删额外需要添加到hosts的域名。
3. 通过爬虫循环爬取[IpAddress](https://www.ipaddress.com/)，得到对应的ip列表，补充到hosts文件中。
4. 替换系统的hosts文件

技术栈：springboot + javafx + webmagic + h2

打包：项目目录下执行 ```mvn jfx:jar```

运行：需要以命令行方式传入github账号密码，例如：
```java jar update-hosts-1.0.jar --git.username=yourGithubUserName --git.password=123456```

![Image text](https://github.com/dantegarden/update-hosts/blob/master/doc/example.png)

龟速完善中......