package ynu.edu.controller;

import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ynu.edu.entity.CommonResult;
import ynu.edu.entity.User;
import ynu.edu.feign.ServiceProviderService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/time")
public class Time {
    @Resource
    private ServiceProviderService serviceProviderService;
    @GetMapping("/getUser/{userId}")
    @TimeLimiter(name = "timeout1", fallbackMethod = "getUserFallback")
    public CompletableFuture<String> getUserById(@PathVariable("userId") Integer userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String messagr="进入方法";
                System.out.println(messagr);
                // 设置超时时间为1秒
                return String.valueOf(serviceProviderService.getUserById(userId));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).orTimeout(1, TimeUnit.SECONDS); // 设置超时时间
    }


    public CompletableFuture<String> getUserFallback(Integer userId, Throwable e) {
        e.printStackTrace();
        String message = "获取用户" + userId + "信息的服务当前被熔断，第一个方法降级限流";
        System.out.println(message);
        return CompletableFuture.completedFuture(message);
    }
}
