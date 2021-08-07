package validator;

import io.github.nambach.excelutil.validator.Error;
import io.github.nambach.excelutil.validator.Validation;
import io.github.nambach.excelutil.validator.builtin.Validator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String name;
    private boolean working;
    private String aboutMe;
    private Float age;
    private String email;

    static final Validation<User> USER_VALIDATION = Validation
            .fromClass(User.class)
            .field("name").validate(v -> v.notNull("Name cannot be null").isString())
            .field("aboutMe").validate(Validator.string()
                                                .lengthBetween(10, 200, "About Me must be between 10 and 200 characters"))
            .field("age").validate(v -> v.isDecimal().notNull()
                                         .min(18, "Age should not be less than 18")
                                         .max(150, "Age should not be greater than 150"))
            .field("email").validate(v -> v.isString()
                                           .isEmail("Email should be valid"))
            .build();

    public static void main(String[] args) {
        User user = new User();
        user.setName("123");
        user.setWorking(true);
//        user.setAboutMe("1234567890");
        user.setAge(18f);
        user.setEmail("a@a.vn");

        Error error = USER_VALIDATION.validateAllFields(user);
        if (error.noError()) {
            System.out.println("No error.");
        } else {
            System.out.println(USER_VALIDATION.validate(user));
        }
        for (Error.Entry entry : error) {
            System.out.println(entry);
        }
    }
}
