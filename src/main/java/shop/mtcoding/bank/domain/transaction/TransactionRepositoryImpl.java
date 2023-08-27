package shop.mtcoding.bank.domain.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

interface Dao {
    List<Transaction> findTransactionList(@Param("accountId") Long accountId, @Param("gubun") String gubun,
                                          @Param("page") Integer page);
}

@RequiredArgsConstructor
public class TransactionRepositoryImpl implements Dao {
    private final EntityManager em;

    @Override
    public List<Transaction> findTransactionList(Long accountId, String gubun, Integer page) {
        // 동적 쿼리 (gubun 값을 가지고 동적쿼리 = DEPOSIT, WITHDRAW, ALL)
        StringBuilder sb = new StringBuilder();
        sb.append("select t from Transaction t ");

        if ("WITHDRAW".equals(gubun)) { // 출금
            sb.append("join fetch t.withdrawAccount wa ");
            sb.append("where t.withdrawAccount.id = :withdrawAccountId ");
        } else if ("DEPOSIT".equals(gubun)) { // 입금
            sb.append("join fetch t.depositAccount da ");
            sb.append("where t.depositAccount.id = :depositAccountId ");
        } else { // ALL, 전체
            sb.append("left join fetch t.withdrawAccount wa ");
            sb.append("left join fetch t.depositAccount da ");
            sb.append("where t.withdrawAccount.id = :withdrawAccountId ");
            sb.append("or ");
            sb.append("t.depositAccount.id = :depositAccountId ");
        }

        String sql = sb.toString();
        TypedQuery<Transaction> query = em.createQuery(sql, Transaction.class);
        if("WITHDRAW".equals(gubun)) {
            query.setParameter("withdrawAccountId", accountId);
        } else if("DEPOSIT".equals(gubun)) {
            query.setParameter("depositAccountId", accountId);
        } else { // ALL
            query.setParameter("withdrawAccountId", accountId);
            query.setParameter("depositAccountId", accountId);
        }

        query.setFirstResult(page * 5);
        query.setMaxResults(5);

        return query.getResultList();
    }
}
