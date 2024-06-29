package ynu.edu.controller;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ynu.edu.entity.CommonResult;
import ynu.edu.entity.User;
import ynu.edu.feign.ServiceProviderService;

import java.sql.SQLDataException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/cart")
public class Cartcontroller {
    @Resource
    private ServiceProviderService serviceProviderService;
    @GetMapping("/getCartById/{userId}")
    @RateLimiter(name = "backendD",fallbackMethod = "getCartByIdDownRate")
    public CommonResult<User> getCartById(@PathVariable("userId") Integer userId) {
        System.out.println("进入第一个方法！");
        return serviceProviderService.getUserById(userId);

    }

    public CommonResult<User> getCartByIdDownRate(Integer userId, Throwable e){
        e.printStackTrace();
        String message = "获取用户"+userId+"信息的服务当前被熔断，第一个方法降级限流";
        System.out.println(message);
        CommonResult<User> result = new CommonResult<User>(400,"fallback",new User());
        return result;
    }

    public CommonResult<User> getCartByIdDown(Integer userId, Throwable e){
        e.printStackTrace();
        String message = "获取用户"+userId+"信息的服务当前被熔断，第一个方法降级";
        System.out.println(message);
        CommonResult<User> result = new CommonResult<User>(400,"fallback",new User());
        return result;
    }

    public CommonResult<User> getCartByIdDown(Integer userId, SQLDataException e){
        e.printStackTrace();
        String message = "请联系管理员，当前数据库异常，因此方法降级";
        System.out.println(message);
        CommonResult<User> result = new CommonResult<>(400,message,new User());
        return result;
    }

    @GetMapping("/getCartByName/{username}")
    @CircuitBreaker(name = "backendB",fallbackMethod = "getCartByNameDown")
    public CommonResult<User> getCartByName(@PathVariable("username") String userName) {
        System.out.println("进入第二个方法！");
        return serviceProviderService.getUserByName(userName);

    }

    public CommonResult<User> getCartByNameDown(String userName, Throwable e){
        e.printStackTrace();
        String message = "获取用户"+userName+"信息的服务被熔断，第二个方法降级";
        System.out.println(message);
        CommonResult<User> result = new CommonResult<User>(400,"fallback",new User());
        return result;
    }



}
