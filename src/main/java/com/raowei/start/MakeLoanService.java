package com.raowei.start;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/5/10.
 */
@Service("makeLoanServer")
public class MakeLoanService {

    public void doMakLoan(String loanId) {
        System.out.println("do make loan : " + loanId);
    }


}
