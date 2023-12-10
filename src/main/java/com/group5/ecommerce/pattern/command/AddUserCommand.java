package com.group5.ecommerce.pattern.command;

import com.group5.ecommerce.model.RegistrationForm;
import com.group5.ecommerce.service.UserService;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class AddUserCommand implements Command{

    private RegistrationForm parameter;

    public AddUserCommand(RegistrationForm parameter){
        this.parameter = parameter;
    }

    @Autowired
    private UserService userService;

    @Override
    public void execute() {
        userService.createNewUser(parameter);
    }
}
