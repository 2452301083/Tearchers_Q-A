package com.appleyk.service.impl;

import com.appleyk.process.ModelProcess;
import com.appleyk.repository.QuestionRepository;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Primary
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    @Resource
    private JdbcTemplate jdbctemplate;

//    添加热门老师；ID name 头像 times 网页链接
//    添加热门搜索：
    private boolean addTea(String name, String touxiang, String tea_lianjie){
        String sql_update = "UPDATE tea_tui SET times = times + 1 where name=?;";
        int up = jdbctemplate.update(sql_update,name);
        if (up==0){//更改的行数为0
            String sql_insert = "INSERT INTO tea_tui (name,touxiang,times,tea_lianjie) VALUES(?,?,?,?);";
            return jdbctemplate.update(sql_insert,name,touxiang,1,tea_lianjie)>0;
        }
        return true;
    }
    private boolean addQuestion(String question){
        String sql_update = "UPDATE hot_question SET times = times + 1 where question=?;";
        if (jdbctemplate.update(sql_update,question)>0){//更改的行数大于0
            return true;
        }else {
            String sql_insert = "INSERT INTO hot_question (question,times) VALUES(?,?);";
            return jdbctemplate.update(sql_insert,question,1)>0;
        }
    }

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public String answer(String question) throws Exception{
        ModelProcess queryprocess = new ModelProcess("D:/pro/hanlp/");

        loadTeacherDict("D:/pro/hanlp/data/dictionary/custom/teachers.txt");

        ArrayList<String> reStrings = queryprocess.analyQuery(question);
//        添加问题到数据库
        addQuestion(question);
        int modelIndex = Integer.valueOf(reStrings.get(0));
//        添加老师到数据库
        if (modelIndex <10 & modelIndex>0){
            String name = reStrings.get(1);
            addTea(name,questionRepository.getTouxaing(name),questionRepository.getTeacherLianjie(name));
        }
        String answer = "null";
        String name = "";
        /**
         * 匹配问题模板
         */
        switch (modelIndex){
            case 0:
                /**
                 * 教师基本信息
                 */
                name = reStrings.get(1);
                List<String> info = questionRepository.getTeacher(name);
                if (info !=null){
                    answer = String.join(";",info);
                }
                break;
            case 1:
                /**
                 * 教师的学院
                 */
                name = reStrings.get(1);
                String xueyuan = questionRepository.getXueyuan(name);
                if (xueyuan != null){
                    answer = xueyuan;
                }
                break;
            case 2:
                /**
                 * 教师的网页链接
                 */
                name = reStrings.get(1);
                String htmlLianjie = questionRepository.getTeacherLianjie(name);
                if (htmlLianjie != null){
                    answer = htmlLianjie;
                }
                break;
            case 3:
                /**
                 * 教师的头像
                 */
                name = reStrings.get(1);
                String touxiang = questionRepository.getTouxaing(name);
                if (touxiang != null){
                    answer = touxiang;
                }
                break;
            case 4:
                /**
                 * 教师的研究方向
                 */
                name = reStrings.get(1);
                List<String> subject = questionRepository.getSubject(name);
                if (subject != null){
                    answer = String.join(";",subject);
                }
                break;
            case 5:
                /**
                 * 教师的职称
                 */
                name = reStrings.get(1);
                List<String> zhicheng = questionRepository.getZhichneg(name);
                if (zhicheng != null){
                    answer = String.join(";",zhicheng);
                }
                break;
            case 6:
                /**
                 * 教师的职位
                 */
                name = reStrings.get(1);
                String zhiwei = questionRepository.getZhiwei(name);
                if (zhiwei != null){
                    answer = zhiwei;
                }
                break;
            case 7:
                /**
                 * 教师的邮箱
                 */
                name = reStrings.get(1);
                String email = questionRepository.getEmail(name);
                if (email != null){
                    answer = email;
                }
                break;
            case 8:
                /**
                 * 教师的固定电话
                 */
                name = reStrings.get(1);
                String tel = questionRepository.getTel(name);
                if (tel != null){
                    answer = tel;
                }
                break;
            case 9:
                /**
                 * 教师的简介
                 */
                name = reStrings.get(1);
                String introduction = questionRepository.getIntroduction(name);
                if (introduction != null){
                    answer = introduction;
                }
                break;
            case 10:
                /**
                 * 当前学院的老师
                 */
                name = reStrings.get(1);
                List<String> teas = questionRepository.getXuyuanTeachers(name);
                if (teas != null){
                    answer = String.join(";",teas);
                }
                break;
            case 11:
                /**
                 * 当前职称的老师
                 */
                name = reStrings.get(1);
                teas = questionRepository.getZhihcnegTeachers(name);
                if (teas != null){
                    answer = String.join(";",teas);
                }
                break;
            case 12:
                /**
                 * 当前研究方向的老师
                 */
                name = reStrings.get(1);
                teas = questionRepository.getSubjectTeachers(name);
                if (teas != null){
                    answer = String.join(";",teas);
                }
                break;
            case 13:
                /**
                 * 教师1 教师2 是否是同一学院
                 */
                String name1 = reStrings.get(1);
                addTea(name1,questionRepository.getTouxaing(name1),questionRepository.getTeacherLianjie(name1));
                String name2 = reStrings.get(2);
                addTea(name2,questionRepository.getTouxaing(name2),questionRepository.getTeacherLianjie(name2));
                String xueyuan1 = questionRepository.getXueyuan(name1);
                String xueyuan2 = questionRepository.getXueyuan(name2);
                if (xueyuan1.equals(xueyuan2)){
                    answer = "他们都是"+xueyuan1+"的老师";
                }else {
                    answer = "他们不是同一个学院的老师，"+name1+":"+xueyuan1+"\n"+name2+":"+xueyuan2;
                }
                break;
            case 14:
                /**
                 * 教师1 教师2 是否同职称
                 */
                name1 = reStrings.get(1);
                name2 = reStrings.get(2);
                addTea(name1,questionRepository.getTouxaing(name1),questionRepository.getTeacherLianjie(name1));
                addTea(name2,questionRepository.getTouxaing(name2),questionRepository.getTeacherLianjie(name2));
                String zhicheng1 = String.join(";",questionRepository.getZhichneg(name1));
                String zhicheng2 = String.join(";",questionRepository.getZhichneg(name2));
                if (zhicheng1.equals(zhicheng2)){
                    if (zhicheng1.equals("null")){
                        answer = "他们都是职称为空";
                    }
                    else{
                        answer = "他们都是职称为"+zhicheng1+"的老师";
                    }
                }else {
                    answer = "他们不是同一职称的老师，"+name1+":"+zhicheng1+"\n"+name2+":"+zhicheng2;
                }
                break;
            case 15:
                /**
                 * 教师1 教师2 是否同一研究方向
                 */
                name1 = reStrings.get(1);
                name2 = reStrings.get(2);
                addTea(name1,questionRepository.getTouxaing(name1),questionRepository.getTeacherLianjie(name1));
                addTea(name2,questionRepository.getTouxaing(name2),questionRepository.getTeacherLianjie(name2));
                String subject1 = String.join(";",questionRepository.getZhichneg(name1));
                String subject2 = String.join(";",questionRepository.getZhichneg(name2));
                if (subject1.equals(subject2)){
                    answer = "他们都是研究方向为"+subject1+"的老师";
                }else {
                    answer = "他们不是同一研究方向的老师，"+name1+":"+subject1+"\n"+name2+":"+subject2;
                }
                break;
            default:
                break;
        }
        System.out.println(answer);
        if (answer != null && !answer.equals("") && !answer.equals("\\N")) {
            return answer;
        } else {
            return "sorry,我没有找到你要的答案";
        }
    }

    /**
     * 加载老师字典
     * @param path
     */
    public void loadTeacherDict(String path){

        File file = new File(path);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            addCustomDictionary(br);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public void addCustomDictionary(BufferedReader br){

        String word;
        try {
            while ((word = br.readLine())!=null){
                CustomDictionary.add(word,"tea 1");
            }
            br.close();
        }catch (NumberFormatException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}