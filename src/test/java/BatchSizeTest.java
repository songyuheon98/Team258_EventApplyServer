//import com.example.team258.kafka.entity.User;
//import com.example.team258.kafka.repository.UserRepository;
//import com.example.team258.kafka.entity.BookApplyDonation;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
//@Transactional
//public class BatchSizeTest {
//
//    private UserRepository userRepository;
//
//    public BatchSizeTest(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Test
//    void batchTest(){
//        //given
//        User user = User.builder()
//                .username("test")
//                .password("test")
//                .build();
//
//        BookApplyDonation bookApplyDonation = BookApplyDonation.builder()
//                .book(null)
//                .build();
//
//        userRepository.save(user);
//
//
//        user.addBookApplyDonation(bookApplyDonation);
//
//
//
//
//
//
//    }
//}
