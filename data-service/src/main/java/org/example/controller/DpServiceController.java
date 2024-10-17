package org.example.controller;

import org.example.dto.Result;
import org.example.entity.DpValue;
import org.example.service.DpService;
import org.springframework.web.bind.annotation.*;

/**
 * @author: xiaoliyu
 * @description: TODO
 * @dateTime: 2024/10/17 14:15
 **/
@RestController
@RequestMapping("/dpService")
public class DpServiceController {

    private final DpService dpService;

    public DpServiceController(DpService dpService) {
        this.dpService = dpService;
    }

    @GetMapping("/dpGet")
    public Result dpGet(@RequestParam String dpName) {
        return Result.success(dpService.dpGet(dpName));
    }

    @PostMapping("/dpSet")
    public Result dpSet(@RequestBody DpValue dpValue) {
        return Result.success(dpService.dpSet(dpValue.getDpName(), dpValue.getValue()));
    }

    @GetMapping("/dpIsExist")
    public Result dpIsExist(@RequestParam String dpName) {
        return Result.success(dpService.dpIsExist(dpName));
    }
}
