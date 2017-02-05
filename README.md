# Spring User Managment

## Configuration
Use annotation `@EnableUserManagment` for enable registration and missing password page.

By extending class `UserManagmentConfigurerAdapter` by override method `configure`, you can change adapters and others class components.

Example:
```java
@EnableUserManagment
public class UserManagmentConfig extends UserManagmentConfigurerAdapter {
    
    @Override
    public void configure(UserManagmentEndpointsConfigurer endpoints) throws Exception {
        // your configuration
    }
}
```

### UserDetailsService
UserDetailsService must be instance of `UserDetailsPasswordResetService` and `UserDetailsActivationService` defined in the library.
For easier implementation, you can use `UserDetailsPasswordResetServiceAbs` and `UserDetailsActivationServiceAbs`, based on litle upgraded `UserDetails` interface.

Example:
```java
@EnableUserManagment
public class UserManagmentConfig extends UserManagmentConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configure(UserManagmentEndpointsConfigurer endpoints) throws Exception {
        endpoints.setUserDetailsService(userDetailsService);
    }
}
```

### Routes
If you want change default routes, use `endpoints.pathMapping(String defaultPath, String newPath)`.

Example:
```java
endpoints
        .pathMapping("/account/activate", "/activate-account.html")
        .pathMapping("/account/registration", "/registration.html")
        .pathMapping("/account/reset", "/reset-account.html")
        .pathMapping("/account/password/reset", "/reset-account-pass.html")
        .pathMapping("/account/password/change", "/changepassword.html");
```

### Views
There are five routes, return ModelAndView objects. Default are:
- `activate/userActivation`
- `activate/userRegistration`
- `password/change`
- `password/resetForm`
- `password/resetRequest`

### Information service
If you want to change system of sending emails, structure or change emails to other service, like SMS etc., implement interface `InformationActivationService`
and `InformationPasswordService`. After implementation, set new services to endpoints, by method `setInformationActivationService` and `setInformationPasswordService`.

Example:
```java
endpoints
        .setInformationActivationService(new InformationActivationService())
        .setInformationPasswordService(new InformationPasswordService());
```


### Registration and other sended data
The base implementation is built only on email (as username) and password. After extend the UserDetails interface further next atributes, you can extend predefined
messangers, which are responsible for processing the requests.

Setup example:
```java
endpoints
        .setUserMessanger(new MyUserMessanger());
```

After processing data, there are validator classes, responsible for successfully registration. They are part of UserDetailsService.

## License
Standard MIT, complete information LICENSE.md