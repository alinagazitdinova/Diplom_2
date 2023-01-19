package utils;

import model.UserNewData;
import org.apache.commons.lang3.RandomStringUtils;

public class UserNewDataGenerator {
    public UserNewData random() {
        return new UserNewData(RandomStringUtils.randomAlphanumeric(10) +"@gmail.com", RandomStringUtils.randomAlphabetic(8));
    }

}
