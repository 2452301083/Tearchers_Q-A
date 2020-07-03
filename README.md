# Tearchers_Q-A
## Spring-Boot集成Neo4j结合Spark的朴素贝叶斯分类器实现基于教师信息（宁波大学、温州大学、杭州电子科技大学）知识图谱的智能问答系统
## 效果展示
### 1.教师基本信息的查询
![images](https://github.com/2452301083/Tearchers_Q-A/blob/master/images/%E5%9B%BE%E7%89%871.png)
![images](https://github.com/2452301083/Tearchers_Q-A/blob/master/images/%E5%9B%BE%E7%89%872.png)
![images](https://github.com/2452301083/Tearchers_Q-A/blob/master/images/%E5%9B%BE%E7%89%873.png)
### 2.查询某学院、某职称、某研究方向的老师
![images](https://github.com/2452301083/Tearchers_Q-A/blob/master/images/%E5%9B%BE%E7%89%874.png)
![images](https://github.com/2452301083/Tearchers_Q-A/blob/master/images/%E5%9B%BE%E7%89%875.png)
![images](https://github.com/2452301083/Tearchers_Q-A/blob/master/images/%E5%9B%BE%E7%89%876.png)
### 3.查询教师1和教师2是否同学院、同职称、同研究方向
![images](https://github.com/2452301083/Tearchers_Q-A/blob/master/images/%E5%9B%BE%E7%89%877.png)
### 4.推荐以往查询次数较多的老师和问题
![images](https://github.com/2452301083/Tearchers_Q-A/blob/master/images/%E5%9B%BE%E7%89%878.png)
![images](https://github.com/2452301083/Tearchers_Q-A/blob/master/images/%E5%9B%BE%E7%89%879.png)
## 项目介绍
### 用到的技术
Python 爬虫；Spring-Boot框架；Neo4j图数据库；hanlp分词；贝叶斯模型；Semantic UI CSS框架；MySQL；
### 获取数据
Python 程序爬取教师信息保存为txt文件，数据条目有：教师姓名、邮箱、电话、头像、网页链接、职位、职称、个人简介、所在学院、研究方向。
通过不断解析页面，分离网页信息得到教师信息，爬取数据的代码如下：
    for url in urls:
    response = requests.get(url,headers=headers).content.decode("utf-8")
    soup = BeautifulSoup(response,'lxml').find_all('div',{'class':'right-daoshi-info'})
    name = isnull(soup[0].find_all('p')[0].find('span').get_text())
    get_img(name,soup)
    title = isnull(soup[0].find_all('p')[2].find('span').get_text())
    ZZDH =  isnull(soup[0].find_all('p')[5].find('span').get_text())
    research_field = isnull(soup[0].find_all('p')[4].find('span').get_text())
    postion = isnull(soup[0].find_all('p')[len(soup[0].find_all('p'))-1].find('span').get_text())
    email = isnull(soup[0].find_all('p')[3].find('span').get_text())
    introduction = ''
    if soup[len(soup)-1].find_all('div',{'class':'zs-major'}):
        ps = soup[len(soup)-1].find_all('div',{'class':'zs-major'})[0].find_all('p')
        for p in ps:
            if p.find('em'):
                introduction = introduction+p.find('em').get_text().strip()+p.find('span').get_text().strip()
            # get_img(name,soup)
            else:
                introduction = 'null'
    else:
        introduction = 'null'
### 创建Neo4j数据库
#### 创建节点、关系的csv文件
![images](https://github.com/2452301083/Tearchers_Q-A/blob/master/images/%E5%9B%BE%E7%89%8710.png)
#### 命令行语句导入数据库
    neo4j-admin import --mode=csv --database=graph.db --nodes importdata\subject.csv --nodes importdata\school.csv --nodes importdata\teacher.csv --nodes importdata\xueyuan.csv --nodes importdata\youbian.csv  --nodes importdata\zhicheng.csv --relationships importdata\school_xueyuan.csv --relationships importdata\tea_subject.csv --relationships importdata\tea_youbian.csv --relationships importdata\tea_zhicheng.csv --relationships importdata\xueyuan_tea.csv --ignore-duplicate-nodes=true --ignore-missing-nodes=true
### 自定义hanlp字典
![images](https://github.com/2452301083/Tearchers_Q-A/blob/master/images/%E5%9B%BE%E7%89%8711.png)
![images](https://github.com/2452301083/Tearchers_Q-A/blob/master/images/%E5%9B%BE%E7%89%8712.png)
### 贝叶斯分类
#### 设计问题模板、问题分类、问题关键词文件
![images](https://github.com/2452301083/Tearchers_Q-A/blob/master/images/%E5%9B%BE%E7%89%8713.png)
#### 加载文件，训练模型
   生成spark对象训练模型：首先创建spark对象，加载训练数据，遍历问句，将问句进行分词处理，创建与关键词数量相同大小的double数组，初始化值为0.0，将问句分词后得到的结果与关键词进行比对，若该词为关键词则在数组的对应位置设置值为1.0。将得到的数组转换为LabeledPoint向量，并保存在LabeledPoint向量列表中，依次进行，将所有训练数据进行转化。调用spark的NaiveBayesModel进行训练模型。

#### 问句分类
### Neo4j查询
   Neo4jRepository是由Spring-Data-Neo4j提供的接口，可以使用@Query注释使用Cypher图形查询语言从Neo4j中检索数据。
    public interface QuestionRepository extends Neo4jRepository<Teacher, Long> {
        /**
         * 0 对应模板0 =》tea 基本信息
         *
         * @param name 教师姓名
         * @return 返回教师的基本信息
         */
        @Query("match(n:TEACHER) where n.name={name} return n")
        List<String> getTeacher(@Param("name") String name);

        /**
         * 1 对应模板1 =》tea 学院
         *
         * @param name 教师名
         * @return 所在学院名称
         */
        @Query("match(n:TEACHER)-[r:PINYONG]->(b:XUEYUAN) where n.name={name} return b.name")
        String getXueyuan(@Param("name") String name);

        /**
         * 2 对应模板2 =》tea 网页链接
         *
         * @param name 教师名
         * @return 教师的网页链接
         */
        @Query("match(n:TEACHER) where n.name={name} return n.tea_lianjie")
        String getTeacherLianjie(@Param("name") String name);
        ......
    }    
