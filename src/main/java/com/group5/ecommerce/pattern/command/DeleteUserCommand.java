package com.group5.ecommerce.pattern.command;

import com.group5.ecommerce.model.RegistrationForm;
import com.group5.ecommerce.model.User;
import com.group5.ecommerce.service.UserService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class DeleteUserCommand implements Command{

    private RegistrationForm parameter;

    public DeleteUserCommand(RegistrationForm parameter){
        this.parameter = parameter;
    }

    @Autowired
    private UserService userService;

    @Override
    public void execute() {
        User user = userService.getUserByEmail(parameter.getEmail());
        if(user != null){
            userService.removeUserById(user.getId());
        }
    }
}
