package com.appleyk.controller;

import com.appleyk.service.impl.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class QuestionController {

    @Autowired
    QuestionService questService;
    @RequestMapping(value = "/query")
    public String query(@RequestParam(value = "question") String question) throws Exception {
        return questService.answer(question);
    }

}
