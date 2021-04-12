# security-jwt-demo

#### 介绍
使用spring-security-jwt完成的单体服务框架


#### 使用说明

1.  配置允许通过的访问路径 参考application.yml里面的路径  
2.  配置访问端点  JwtAuthenticationFilter  设置项目登录路径(参见构造器)，登录将走attemptAuthentication()方法，然后路由到UserDetailsServiceImpl里的loadByusername方法，根据系统系统存在的加密方式匹配User对象的密码，参考SecurityConfig 中的passwordEncoder()方法。  
3.  用户信息校验成功过后路由到JwtAuthenticationFilter中的successfulAuthentication()方法，在这个地方获取用户的角色权限信息，角色信息以“ROLE_”开头，权限不需要将角色权限信息封装到token中。因为MVC是sevlet模型，所以方法结束前仍可以获取请求和响应。然后通过输出流返回数据。  
4.  用户登陆时请求将通过JwtAuthenticationTokenFilter校验token，JwtAuthorizationFilter对token进行解密并封装用户权限信息，完成后可匹配SecurityConfig中的路径权限信息。  
5.  用户登陆失败请求到达JwtAuthenticationEntryPoint 其登录的账号密码可能存在问题，账号状态也可能存在问题，需要根据具体情况返回输出结果。  
6.  用户登录成功过后请求需要携带token，对于个别路径配置了hasRole或者hasAuthority其token的中需要包含"ROLE_user" / "read
" 配置的角色或者权限，如果没有相应的权限则请求到达JwtAccessDeniedHandler，返回给用户403状态  

