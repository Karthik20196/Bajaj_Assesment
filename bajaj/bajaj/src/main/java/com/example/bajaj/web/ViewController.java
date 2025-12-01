package com.example.bajaj.web;

import com.example.bajaj.service.ResultStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    private final ResultStore resultStore;

    public ViewController(ResultStore resultStore) {
        this.resultStore = resultStore;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("query",
                resultStore.getFinalQuery() == null ?
                        "Query not generated yet" :
                        resultStore.getFinalQuery());

        return "index";
    }
}
