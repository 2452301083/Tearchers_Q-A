package com.appleyk.controller;

import com.appleyk.entity.hot_question;
import com.appleyk.entity.tea_tui;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Controller
public class PageIndexController {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String index(ModelMap map) {
        String sql_1 = "SELECT * FROM tea_tui order by times desc limit 6";
        List<tea_tui> teaList = jdbcTemplate.query(sql_1, new RowMapper<tea_tui>() {
            tea_tui tea = null;
            @Override
            public tea_tui mapRow(ResultSet rs, int i) throws SQLException {
                tea = new tea_tui();
                tea.setName(rs.getString("name"));
                tea.setTea_lianjie(rs.getString("tea_lianjie"));
                tea.setTouxiang(rs.getString("touxiang"));
//                tea.setTimes(rs.getInt("times"));
//                System.out.println(tea.getId() + tea.getName() + tea.getTea_lianjie() + tea.getTouxiang());
                return tea;
            }
        });
        map.addAttribute("teas", teaList);
        System.out.println(teaList.size());

        String sql_2 = "SELECT * FROM hot_question order by times desc limit 6";
        List<hot_question> questionList = jdbcTemplate.query(sql_2, new RowMapper<hot_question>() {
            hot_question question = null;
            @Override
            public hot_question mapRow(ResultSet rs, int i) throws SQLException {
                question = new hot_question();
                question.setQuestion(rs.getString("question"));
//                question.setAnswer(rs.getString("answer"));
                return question;
            }
        });
        map.addAttribute("questions",questionList);
        return "index";
    }
}
