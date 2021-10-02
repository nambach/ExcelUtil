package validator;

import io.github.nambach.excelutil.validator.FieldError;
import io.github.nambach.excelutil.validator.ObjectError;
import io.github.nambach.excelutil.validator.Validator;
import io.github.nambach.excelutil.validator.builtin.TypeValidator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    static final Validator<User> USER_VALIDATOR = Validator
            .fromClass(User.class)
            .on(f -> f.field("name")
                      .validate(v -> v.notNull("Name cannot be null").isString()))
            .on(f -> f.field("any")
                      .customExtract(user -> user.name.length())
                      .validate(v -> v.customValidator(o -> true, "")))
            .on(f -> f.field("aboutMe")
                      .validate(TypeValidator.string()
                                             .lengthBetween(10, 200, "About Me must be between 10 and 200 characters")))
            .on(f -> f.field("age")
                      .validate(v -> v.isDecimal().notNull()
                                      .min(18, "Age should not be less than 18")
                                      .max(150, "Age should not be greater than 150")))
            .on(f -> f.field("email")
                      .validate(v -> v.isString()
                                      .isEmail("Email should be valid")));
    private String name;
    private boolean working;
    private String aboutMe;
    private Float age;
    private String email;

    public static void main(String[] args) {
        User user = new User();
        user.setName("123");
        user.setWorking(true);
//        user.setAboutMe("1234567890");
        user.setAge(18f);
        user.setEmail("a@.vn");

        ObjectError objectError = USER_VALIDATOR.validate(user);
        if (objectError.noError()) {
            System.out.println("No error.");
        } else {
            System.out.println(USER_VALIDATOR.quickValidate(user));
        }
        for (FieldError fieldError : objectError) {
            System.out.println(fieldError);
        }
    }
}
