package shop.mtcoding.bank.domain.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryImplTest extends DummyObject {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        autoincrementReset();
        dataSetting();
    }

    @DisplayName("")
    @Test
    void findAll1() {
        //given
        //when
        List<Transaction> transactions = transactionRepository.findAll();

        //then
        transactions.forEach((t) -> {
            System.out.println("테스트 : " + t.getId());
            System.out.println("테스트 : " + t.getSender());
            System.out.println("테스트 : " + t.getReceiver());
            System.out.println("테스트 : " + t.getGubun());
            System.out.println("==========================");
        });
    }

    @DisplayName("")
    @Test
    void findAll2() {
        //given
        //when
        List<Transaction> transactions = transactionRepository.findAll();

        //then
        transactions.forEach((t) -> {
            System.out.println("테스트 : " + t.getId());
            System.out.println("테스트 : " + t.getSender());
            System.out.println("테스트 : " + t.getReceiver());
            System.out.println("테스트 : " + t.getGubun());
            System.out.println("==========================");
        });
    }

    private void dataSetting() {
        User ssar = userRepository.save(newUser("ssar", "쌀"));
        User cos = userRepository.save(newUser("cos", "코스,"));
        User love = userRepository.save(newUser("love", "러브"));
        User admin = userRepository.save(newUser("admin", "관리자"));

        Account ssarAccount1 = accountRepository.save(newAccount(1111L, ssar));
        Account cosAccount = accountRepository.save(newAccount(2222L, cos));
        Account loveAccount = accountRepository.save(newAccount(3333L, love));
        Account ssarAccount2 = accountRepository.save(newAccount(4444L, ssar));

        Transaction withdrawTransaction1 = transactionRepository
                .save(newWithdrawTransaction(ssarAccount1, accountRepository)); // 100원 출금 -> 900원
        Transaction depositTransaction1 = transactionRepository
                .save(newDepositTransaction(ssarAccount1, accountRepository)); // 100원 입금 -> 1000원
        Transaction transferTransaction1 = transactionRepository
                .save(newTransferTransaction(ssarAccount1, cosAccount, accountRepository)); // 100원 이체 (ssar : 900원, cos : 1100원)
        Transaction transferTransaction2 = transactionRepository
                .save(newTransferTransaction(ssarAccount1, loveAccount, accountRepository)); // 100원 이체 (ssar : 800원, love : 1100원)
        Transaction transferTransaction3 = transactionRepository
                .save(newTransferTransaction(cosAccount, ssarAccount1, accountRepository)); // 100원 이체, (cos : 1000원, ssar : 900원)
    }

    // 전체 테스트 실행시 auto increase 초기화
    private void autoincrementReset() {
        this.em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN `id` RESTART WITH 1")
                .executeUpdate();
        this.em.createNativeQuery("ALTER TABLE account_tb ALTER COLUMN `id` RESTART WITH 1")
                .executeUpdate();
        this.em.createNativeQuery("ALTER TABLE transaction_tb ALTER COLUMN `id` RESTART WITH 1")
                .executeUpdate();
    }
}