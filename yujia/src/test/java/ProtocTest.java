import com.twist.netty.protobuf.UserMsg;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-04 15:17
 **/
@RunWith(JUnit4.class)
@Slf4j
public class ProtocTest {

    @Test
    public void UserMsgTest() throws IOException {
        UserMsg.User.Builder userInfo = UserMsg.User.newBuilder();
        userInfo.setId(1);
        userInfo.setName("yingjie");
        userInfo.setAge(25);
        UserMsg.User user = userInfo.build();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        user.writeTo(outputStream);

        byte[] bytes = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        UserMsg.User userInfo1 = UserMsg.User.parseFrom(inputStream);
        log.info("id:" + userInfo1.getId());
        log.info("name:" + userInfo1.getName());
        log.info("age:" + userInfo1.getAge());
    }
}
