package validator;

import io.github.nambach.excelutil.validator.Error;
import io.github.nambach.excelutil.validator.Validation;
import io.github.nambach.excelutil.validator.Validator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String name;
    private boolean working;
    private String aboutMe;
    private int age;
    private String email;

    static final Validation<User> USER_VALIDATION = Validation
            .fromClass(User.class)
            .field("name").validate(v -> v.notNull("Name cannot be null"))
            .field("aboutMe").validate(Validator.string()
                                                .lengthBetween(10, 200, "About Me must be between 10 and 200 characters"))
            .field("age").validate(v -> v.isInteger()
                                         .min(18, "Age should not be less than 18")
                                         .max(150, "Age should not be greater than 150"))
            .field("email").validate(v -> v.isString().isEmail("Email should be valid"))
            .build();

    public static void main(String[] args) {
        User user = new User();
        user.setName("123");
        user.setWorking(true);
        user.setAboutMe("1234567890");
        user.setAge(80);
        user.setEmail("a@mail.com");

        Error error = USER_VALIDATION.validateFull(user);
        if (error.noError()) {
            System.out.println("No error.");
            String msg = Validator.decimal().between(0, 2).validate(1.1);
            System.out.println(msg);
        }
        for (Error.Entry entry : error) {
            System.out.printf("field '%s'%n", entry.getFieldName());
            for (String message : entry.getMessages()) {
                System.out.printf(" - %s%n", message);
            }
        }
    }
}
