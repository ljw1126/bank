package shop.mtcoding.bank.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // TODO. 리팩토링, 계좌/소유자 확인시에 쿼리가 두번 나가기 때문에
    Optional<Account> findByNumber(Long number);

    // jpa query method
    // select * from account where user_id = :id;
    List<Account> findByUser_id(Long id);
}
