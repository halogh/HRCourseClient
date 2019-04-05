# HRCourseClient
#### 利用jsoup爬取华软教务系统数据制作一个简易的华软android课表

#### 原理：使用jsoup爬取华软教务系统数据，connect()方法连接教务系统，直接利用cookie连接学生课表页面，解析返回的HTML页面获取该用户的全部课程并封装为java对象，再存入到数据库中，然后查数据库显示到课表。

效果截图：

![登录](C:\Users\zhuang\Desktop\截图\登录.JPG)

![课表](C:\Users\zhuang\Desktop\截图\课表.JPG)

