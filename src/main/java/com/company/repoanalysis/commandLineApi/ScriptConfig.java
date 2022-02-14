package com.company.repoanalysis.commandLineApi;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class ScriptConfig {


//    @Value("${file.python}")
    private String python = "C:\\Python27\\python";

//    @Value("${file.cloc}")
    private String cloc = "C:\\Users\\A92937914\\cloc\\cloc-master\\cloc";

//    @Value("${file.perl}")
    private String perl = "C:\\Strawberry\\perl\\bin\\perl";
}
