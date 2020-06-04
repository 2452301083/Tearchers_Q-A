package com.appleyk.repository;

import com.appleyk.node.Movie;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 基于教师信息知识图谱的自问自答的查询接口
 */
public interface QuestionRepository extends Neo4jRepository<Movie, Long> {

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

    /**
     * 3 对应模板3 =》tea 头像
     *
     * @param name 教师名
     * @return 教师的头像链接
     */
    @Query("match(n:TEACHER) where n.name={name} return n.touxaing")
    String getTouxaing(@Param("name") String name);

    /**
     * 4 对应模板4 =》tea 研究方向
     *
     * @param name 教师名
     * @return 教师的研究方向
     */
    @Query("match(n:TEACHER)-[r:YANJIU]->(b:SUBJECT) where n.name={name} return b.name")
    List<String> getSubject(@Param("name") String name);

    /**
     * 5 对应模板5 =》tea 职称
     *
     * @param name 教师名
     * @return 教师的职称
     */
    @Query("match(n:TEACHER)-[r:ZHICHENG]->(b:ZHICHENG) where n.name={name} return b.name")
    List<String> getZhichneg(@Param("name") String name);

    /**
     * 6 对应模板6 =》tea 职位
     *
     * @param name 教师名
     * @return 教师的职位
     */
    @Query("match(n:TEACHER) where n.name={name} return n.zhiwei")
    String getZhiwei(@Param("name") String name);

    /**
     * 7 对应模板7 =》tea 邮箱
     *
     * @param name 教师名
     * @return 教师的邮箱
     */
    @Query("match(n:TEACHER) where n.name={name} return n.email")
    String getEmail(@Param("name") String name);

    /**
     * 8 对应模板8 =》tea 固定电话
     *
     * @param name 教师名
     * @return 教师的固定电话
     */
    @Query("match(n:TEACHER) where n.name={name} return n.tel")
    String getTel(@Param("name") String name);

    /**
     * 9 对应模板9 =》tea 简介
     *
     * @param name 教师名
     * @return 教师的简介
     */
    @Query("match(n:TEACHER) where n.name={name} return n.introduction")
    String getIntroduction(@Param("name") String name);

    /**
     * 10 对应模板10 =》xy 教师
     *
     * @param name 学院名
     * @return 当前学院的老师名
     */
    @Query("match(n:TEACHER)-[r:PINYONG]->(b:XUEYUAN) where b.name={name} return n.name")
    List<String> getXuyuanTeachers(@Param("name") String name);

    /**
     * 11 对应模板11 =》zhic 教师
     *
     * @param name 职称
     * @return 当前职称的老师名
     */
    @Query("match(n:TEACHER)-[r:ZHICHENG]->(b:ZHICHENG) where b.name={name} return n.name")
    List<String> getZhihcnegTeachers(@Param("name") String name);

    /**
     * 12 对应模板12 =》sub 教师
     *
     * @param name 研究方向
     * @return 当前研究方向的老师名
     */
    @Query("match(n:TEACHER)-[r:YANJIU]->(b:SUBJECT) where b.name={name} return n.name")
    List<String> getSubjectTeachers(@Param("name") String name);
}
