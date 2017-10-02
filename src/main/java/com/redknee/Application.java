package com.redknee;

import com.jcraft.jsch.JSchException;
import com.redknee.cc.ClearCaseCmdCommand;
import com.redknee.cc.ClearCaseCommand;
import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws Exception {
		ClearCaseCmdCommand clearCaseCommand = new ClearCaseCmdCommand();
		clearCaseCommand.checkout();

//		SpringApplication.run(Application.class, args);
	}
}
