# DeltaUpdateLib 增量更新工具
> 使用BSDiff差分工具实现的增量更新工具库，支持加固

## 1. 使用

#### 1.1 生成差分包

> 差分项目可参考另一个项目：[GDiffTool](https://github.com/gzasgjq/GDiffTool)

差分工具下载：[DiffTool](diffTools/DiffTool.jar)

使用：

```
使用: java -jar GDiffTool.jar <method> <oldFile> <newFile> <patchFile>
参数说明：
    method:
        diff: 生成差分包
        patch: 合成差分包

    oldFile: 原始文件
    newFile: 新文件
    patchFile: 差分包
```

#### 1.2 合成Apk安装包

DeltaUpdatesUtils提供了异步、同步、Rx三种方法合成Apk安装包，并且都支持MD5值校验

生成的Apk文件存放在App的Cache下

## 2.后期计划

后期考虑添加后端发布平台，整合成一个开源的增量更新平台。