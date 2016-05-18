# MyAfinal
## 自己讲finalHttp进行学习重写

## 针对FinlaHttp 模块   使用android Studio
### 要解决的问题 StringEntityHandler.java文件中
#### *long count = entity.getContentLength();* 返回的是-1
#### 服务器返回的头信息
###### Server -------- nginx/1.6.0 
###### Content-Type -------- application/json
###### Transfer-Encoding -------- chunked
###### Connection -------- keep-alive
###### X-Powered-By -------- PHP/5.5.15-1+deb.sury.org~trusty+1
###### Cache-Control -------- no-cache

**已经完成**

