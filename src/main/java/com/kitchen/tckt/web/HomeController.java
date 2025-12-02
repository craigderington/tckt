package com.kitchen.tckt.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final String podName;
    private final String nodeName;

    public HomeController(
            @Value("${POD_NAME:unknown-pod}") String podName,
            @Value("${NODE_NAME:unknown-node}") String nodeName) {
        this.podName = podName;
        this.nodeName = nodeName;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("podName", podName);
        model.addAttribute("nodeName", nodeName);
        return "index";
    }
}
