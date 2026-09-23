package io.github.genkidoudou.web.controller;

import io.github.genkidoudou.common.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class IndexController {


    @GetMapping("/")
    public R index() {
        
        return R.ok();
    }
}
