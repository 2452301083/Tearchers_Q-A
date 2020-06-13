package com.appleyk.process;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;

import java.io.*;
import java.util.*;

/**
 * Spark贝叶斯分类器 + HanLP分词器 + 实现问题语句的抽象+模板匹配+关键性语句还原
 *
 * @blob http://blog.csdn.net/appleyk
 * @date 2018年5月9日-上午10:07:52
 */
public class ModelProcess {

    /**
     * 分类标签号和问句模板对应表
     */
    Map<Double, String> questionsPattern;

    /**
     * Spark贝叶斯分类器
     */
    NaiveBayesModel nbModel;

    /**
     * 词语和下标的对应表   == 词汇表
     */
    Map<String, Integer> vocabulary;

    /**
     * 关键字与其词性的map键值对集合 == 句子抽象
     */
    Map<String, String> abstractMap;

    /**
     * 指定问题question及字典的txt模板所在的根目录
     */
    String rootDirPath = "D:/pro/hanlp";

    /**
     * 分类模板索引
     */
    int modelIndex = 0;

    public ModelProcess() throws Exception {
//        questionsPattern = loadQuestionsPattern();
//        vocabulary = loadVocabulary();
        nbModel = loadClassifierModel();
    }

    public ModelProcess(String rootDirPath) throws Exception {
        this.rootDirPath = rootDirPath + '/';
        System.out.println("加载问题模板 !");
        questionsPattern = loadQuestionsPattern();
        vocabulary = loadVocabulary();
        nbModel = loadClassifierModel();
        System.out.println("加载模型 !");
    }

    public ArrayList<String> analyQuery(String queryString) throws Exception {

        /**
         * 打印问句
         */
        System.out.println("原始句子：" + queryString);
        System.out.println("========HanLP开始分词========");

        /**
         * 抽象句子，利用HanPL分词，将关键字进行词性抽象
         */
        String abstr = queryAbstract(queryString);
        System.out.println("句子抽象化结果：" + abstr);// 徐小良 的 基本 信息

        /**
         * 将抽象的句子与spark训练集中的模板进行匹配，拿到句子对应的模板
         */
        String strPatt = queryClassify(abstr);
        System.out.println("句子套用模板结果：" + strPatt); // tea 基本信息

        /**
         * 模板还原成句子，此时问题已转换为我们熟悉的操作
         */
        String finalPattern = queryExtenstion(strPatt);
        System.out.println("原始句子替换成系统可识别的结果：" + finalPattern);// 徐小良 基本信息

        ArrayList<String> resultList = new ArrayList<String>();
        resultList.add(String.valueOf(modelIndex));
        String[] finalPattArray = finalPattern.split(" ");
        for (String word : finalPattArray) {
            resultList.add(word);
        }
        return resultList;
    }

    // 句子抽象化
    private String queryAbstract(String querySentence) {
        Segment segment = HanLP.newSegment().enableCustomDictionaryForcing(true);
        List<Term> terms = segment.seg(querySentence);
        String abstractQuery = "";
        abstractMap = new HashMap<String, String>();
        int teaCount=0;
        for (Term term : terms) {
            String word = term.word;
            String termStr = term.toString();
            if (termStr.contains("tea")|termStr.contains("nr")) {
                if(0 == teaCount){
                    abstractQuery += "tea1 ";
                    abstractMap.put("tea1", word);
                    teaCount+=1;
                }
                else{
                    abstractQuery += "tea2 ";
                    abstractMap.put("tea2", word);
                }
            } else if (termStr.contains("xy")) {
                abstractQuery += "xy ";
                abstractMap.put("xy", word);
            } else if (termStr.contains("zhic")) {
                abstractQuery += "zhic";
                abstractMap.put("zhic", word);
            } else if (termStr.contains("sub")){
                abstractQuery += "sub";
                abstractMap.put("sub",word);
            } else {
                abstractQuery += word + " ";
            }
        }
        System.out.println("========HanLP分词结束========");
        return abstractQuery;
    }

    // 句子还原
    private String queryExtenstion(String queryPattern) {
        Set<String> set = abstractMap.keySet();
        System.out.println(set);
        for (String key : set) {
            //如果句子模板中含有抽象的词性
            if (queryPattern.contains(key)) {
                //则替换抽象词性为具体的值
                String value = abstractMap.get(key);
                System.out.println(key+value);
                queryPattern = queryPattern.replace(key, value);
            }
        }
        String extendedQuery = queryPattern;
        /**
         * 当前句子处理完，抽象map清空释放空间并置空，等待下一个句子的处理
         */
        abstractMap.clear();
        abstractMap = null;
        return extendedQuery;
    }
    //加载词汇表 == 关键特征 == 与HanLP分词后的单词进行匹配  @return
    private Map<String, Integer> loadVocabulary() {
        Map<String, Integer> vocabulary = new HashMap<String, Integer>();
        File file = new File(rootDirPath + "question/vocabulary.txt");
        BufferedReader br = null;
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        br = new BufferedReader(isr);
        String line;
        try {
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(":");
                int index = Integer.parseInt(tokens[0]);
                String word = tokens[1];
                vocabulary.put(word, index);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vocabulary;
    }
    //加载文件，并读取内容返回@param filename@return@throws IOException
    private String loadFile(String filename) throws IOException {
        File file = new File(rootDirPath + filename);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        //BufferedReader br = new BufferedReader(new FileReader(file));
        String content = "";
        String line;
        while ((line = br.readLine()) != null) {
            /**
             * 文本的换行符暂定用"`"代替
             */
            content += line + "`";
        }
        /**
         * 关闭资源
         */
        br.close();
        return content;
    }
    //句子分词后与词汇表进行key匹配转换为double向量数组@param sentence@return@throws Exception
    private double[] sentenceToArrays(String sentence) throws Exception {
        double[] vector = new double[vocabulary.size() + 1];
        /*模板对照词汇表的大小进行初始化，全部为0.0*/
        for (int i = 0; i < vocabulary.size(); i++) {
            vector[i] = 0;
        }
        /*HanLP分词，拿分词的结果和词汇表里面的关键特征进行匹配*/
        Segment segment = HanLP.newSegment();
        List<Term> terms = segment.seg(sentence);
        for (Term term : terms) {
            String word = term.word;
            /*如果命中，0.0 改为 1.0*/
            if (vocabulary.containsKey(word)) {
                int index = vocabulary.get(word);
//                System.out.println(index);
                vector[index] = 1;
            }
        }
        return vector;
    }
    /*Spark朴素贝叶斯(naiveBayes),对特定的模板进行加载并分类
     ** 欲了解Spark朴素贝叶斯，可参考地址：https://blog.csdn.net/appleyk/article/details/80348912*/
    private NaiveBayesModel loadClassifierModel() throws Exception {
        /**
         * 生成Spark对象
         * 一、Spark程序是通过SparkContext发布到Spark集群的
         * Spark程序的运行都是在SparkContext为核心的调度器的指挥下进行的
         * Spark程序的结束是以SparkContext结束作为结束
         * JavaSparkContext对象用来创建Spark的核心RDD的
         * 注意：第一个RDD,一定是由SparkContext来创建的
         *
         * 二、SparkContext的主构造器参数为 SparkConf
         * SparkConf必须设置appname和master，否则会报错
         * spark.master   用于设置部署模式
         * local[*] == 本地运行模式[也可以是集群的形式]，如果需要多个线程执行，可以设置为local[2],表示2个线程 ，*表示多个
         * spark.app.name 用于指定应用的程序名称  ==
         **/
        /**
         * 训练集生成
         * labeled point 是一个局部向量，要么是密集型的要么是稀疏型的
         * 用一个label/response进行关联。在MLlib里，labeled points 被用来监督学习算法
         * 我们使用一个double数组来存储一个label，因此我们能够使用labeled points进行回归和分类
         */
        SparkConf conf = new SparkConf().setAppName("NaiveBayesTest").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        List<LabeledPoint> train_list = new LinkedList<LabeledPoint>();
        String[] sentences = null;
//        训练模型的才需要执行的代码
//        String scoreQuestions = loadFile("question/【0】基本信息.txt");
//        sentences = scoreQuestions.split("`");
//        for (String sentence : sentences) {
//            double[] array = sentenceToArrays(sentence);
//            LabeledPoint train_one = new LabeledPoint(0.0, Vectors.dense(array));
//            train_list.add(train_one);
//        }
//
//        String timeQuestions = loadFile("question/【1】学院.txt");
//        sentences = timeQuestions.split("`");
//        for (String sentence : sentences) {
//            double[] array = sentenceToArrays(sentence);
//            LabeledPoint train_one = new LabeledPoint(1.0, Vectors.dense(array));
//            train_list.add(train_one);
//        }
//
//        String styleQuestions = loadFile("question/【2】网页.txt");
//        sentences = styleQuestions.split("`");
//        for (String sentence : sentences) {
//            double[] array = sentenceToArrays(sentence);
//            LabeledPoint train_one = new LabeledPoint(2.0, Vectors.dense(array));
//            train_list.add(train_one);
//        }
//        /**
//         * 论文属于哪个学科
//         */
//        String withMovies = loadFile("question/【3】头像.txt");
//        sentences = withMovies.split("`");
//        for (String sentence : sentences) {
//            double[] array = sentenceToArrays(sentence);
//            LabeledPoint train_one = new LabeledPoint(3.0, Vectors.dense(array));
//            train_list.add(train_one);
//        }
//
//        /**
//         * 论文发表在什么报刊
//         */
//        String countMovies = loadFile("question/【4】研究方向.txt");
//        sentences = countMovies.split("`");
//        for (String sentence : sentences) {
//            double[] array = sentenceToArrays(sentence);
//            LabeledPoint train_one = new LabeledPoint(4.0, Vectors.dense(array));
//            train_list.add(train_one);
//        }
//        /**
//         * 作者发布了哪些论文
//         */
//        String count = loadFile("question/【5】职称.txt");
//        sentences = count.split("`");
//        for (String sentence : sentences) {
//            double[] array = sentenceToArrays(sentence);
//            LabeledPoint train_one = new LabeledPoint(5.0, Vectors.dense(array));
//            train_list.add(train_one);
//        }
//        String countfirst = loadFile("question/【6】职位.txt");
//        sentences = countfirst.split("`");
//        for (String sentence : sentences) {
//            double[] array = sentenceToArrays(sentence);
//            LabeledPoint train_one = new LabeledPoint(6.0, Vectors.dense(array));
//            train_list.add(train_one);
//        }
//        String countsecond = loadFile("question/【7】邮箱.txt");
//        sentences = countsecond.split("`");
//        for (String sentence : sentences) {
//            double[] array = sentenceToArrays(sentence);
//            LabeledPoint train_one = new LabeledPoint(7.0, Vectors.dense(array));
//            train_list.add(train_one);
//        }
//        String countthird = loadFile("question/【8】固定电话.txt");
//        sentences = countthird.split("`");
//        for (String sentence : sentences) {
//            double[] array = sentenceToArrays(sentence);
//            LabeledPoint train_one = new LabeledPoint(8.0, Vectors.dense(array));
//            train_list.add(train_one);
//        }
//        String countfourth = loadFile("question/【9】简介.txt");
//        sentences = countfourth.split("`");
//        for (String sentence : sentences) {
//            double[] array = sentenceToArrays(sentence);
//            LabeledPoint train_one = new LabeledPoint(9.0, Vectors.dense(array));
//            train_list.add(train_one);
//        }
//        String countjournal = loadFile("question/【10】学院的老师.txt");
//        sentences = countjournal.split("`");
//        for (String sentence : sentences) {
//            double[] array = sentenceToArrays(sentence);
//            LabeledPoint train_one = new LabeledPoint(10.0, Vectors.dense(array));
//            train_list.add(train_one);
//        }
//        String whichsub = loadFile("question/【11】职称的老师.txt");
//        sentences = whichsub.split("`");
//        for (String sentence : sentences) {
//            double[] array = sentenceToArrays(sentence);
//            LabeledPoint train_one = new LabeledPoint(11.0, Vectors.dense(array));
//            train_list.add(train_one);
//        }
//        String whoyj = loadFile("question/【12】研究方向的老师.txt");
//        sentences = whoyj.split("`");
//        for (String sentence : sentences) {
//            double[] array = sentenceToArrays(sentence);
//            LabeledPoint train_one = new LabeledPoint(12.0, Vectors.dense(array));
//            train_list.add(train_one);
//        }
//        String xyis = loadFile("question/【13】老师是否同学院.txt");
//        sentences = xyis.split("`");
//        for (String sentence : sentences) {
//            double[] array = sentenceToArrays(sentence);
//            LabeledPoint train_one = new LabeledPoint(13.0, Vectors.dense(array));
//            train_list.add(train_one);
//        }
//        String zcis = loadFile("question/【14】老师是否同职称.txt");
//        sentences = zcis.split("`");
//        for (String sentence : sentences) {
//            double[] array = sentenceToArrays(sentence);
//            LabeledPoint train_one = new LabeledPoint(14.0, Vectors.dense(array));
//            train_list.add(train_one);
//        }
//        String yjis = loadFile("question/【15】老师是否同研究方向.txt");
//        sentences = yjis.split("`");
//        for (String sentence : sentences) {
//            double[] array = sentenceToArrays(sentence);
//            LabeledPoint train_one = new LabeledPoint(15.0, Vectors.dense(array));
//            train_list.add(train_one);
//        }
//        /**
//         * SPARK的核心是RDD(弹性分布式数据集)
//         * Spark是Scala写的,JavaRDD就是Spark为Java写的一套API
//         * JavaSparkContext sc = new JavaSparkContext(sparkConf);    //对应JavaRDD
//         * SparkContext	    sc = new SparkContext(sparkConf)    ;    //对应RDD
//         */
////        System.out.println(train_list.size());
//        JavaRDD<LabeledPoint> trainingRDD = sc.parallelize(train_list);//切分partition
//        NaiveBayesModel nb_model = NaiveBayes.train(trainingRDD.rdd());
//
        String output_dir = "myNaiveBayesModel";
        SparkContext sc1 = JavaSparkContext.toSparkContext(sc);
//        nb_model.save(sc1,output_dir);
        /**
         * 记得关闭资源
         */
        NaiveBayesModel nb_model = NaiveBayesModel.load(sc1,output_dir);
        sc.close();
        /**
         * 返回贝叶斯分类器
         */
        return nb_model;
    }
    //加载问题模板 == 分类器标签
    private Map<Double, String> loadQuestionsPattern() {
        Map<Double, String> questionsPattern = new HashMap<Double, String>();
        File file = new File(rootDirPath + "question/question_classification.txt");
        BufferedReader br = null;
        InputStreamReader isr = null;
        try {
//            将字符读取流对象作为参数传递给缓冲对象的构造函数
//            br = new BufferedReader(new FileReader(file));
            isr = new InputStreamReader(new FileInputStream(file), "utf-8");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        读取文件
        br = new BufferedReader(isr);
        String line;
        try {
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(":");
                double index = Double.valueOf(tokens[0]);
                String pattern = tokens[1];
                questionsPattern.put(index, pattern);//将键值对放入map
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questionsPattern;
    }
    //贝叶斯分类器分类的结果，拿到匹配的分类标签号，并根据标签号返回问题的模板
    private String queryClassify(String sentence) throws Exception {
//        System.out.println(sentence);
        double[] testArray = sentenceToArrays(sentence);
        Vector v = Vectors.dense(testArray);

        /**
         * 对数据进行预测predict
         * 句子模板在 spark贝叶斯分类器中的索引【位置】
         * 根据词汇使用的频率推断出句子对应哪一个模板
         */
        double index = nbModel.predict(v);
        modelIndex = (int) index;
        System.out.println("the model index is " + index);
        Vector vRes = nbModel.predictProbabilities(v);
        System.out.println("问题模板分类【0】概率：" + vRes.toArray()[0]);
        System.out.println("问题模板分类【1】概率：" + vRes.toArray()[1]);
        System.out.println("问题模板分类【2】概率：" + vRes.toArray()[2]);
        System.out.println("问题模板分类【3】概率：" + vRes.toArray()[3]);
        System.out.println("问题模板分类【4】概率：" + vRes.toArray()[4]);
        System.out.println("问题模板分类【5】概率：" + vRes.toArray()[5]);
        System.out.println("问题模板分类【6】概率：" + vRes.toArray()[6]);
        System.out.println("问题模板分类【7】概率：" + vRes.toArray()[7]);
        System.out.println("问题模板分类【8】概率：" + vRes.toArray()[8]);
        System.out.println("问题模板分类【9】概率：" + vRes.toArray()[9]);
        System.out.println("问题模板分类【10】概率：" + vRes.toArray()[10]);
        System.out.println("问题模板分类【11】概率：" + vRes.toArray()[11]);
		System.out.println("问题模板分类【12】概率："+vRes.toArray()[12]);
        System.out.println("问题模板分类【13】概率："+vRes.toArray()[13]);
        System.out.println("问题模板分类【14】概率："+vRes.toArray()[14]);
        System.out.println("问题模板分类【15】概率："+vRes.toArray()[15]);
//        System.out.println("问题模板分类【13】概率："+vRes.toArray()[13]);
        return questionsPattern.get(index);
    }
    public static void main(String[] agrs) throws Exception {
        //System.out.println("Hello World !");
        String rootDirPath = "D:/pro/hanlp";

        ModelProcess model = new ModelProcess(rootDirPath);
        //System.out.println("训练结束 !");
        Scanner sc = new Scanner(System.in);
        String question = sc.nextLine();
        while(!question.equals("end")){
            ArrayList<String> resultList = new ArrayList<String>();
            resultList = model.analyQuery(question);
            System.out.println(resultList);
            question = sc.nextLine();
        }

        //resultList = model.analyQuery("我想查看范婷老师的论文");
//        for (int i = 0; i < resultList.size(); i++) {
//            System.out.println("resultList [" + i + "] = " + resultList.get(i));
//        }
    }
}
