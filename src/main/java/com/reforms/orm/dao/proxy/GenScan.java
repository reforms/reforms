package com.reforms.orm.dao.proxy;

import sun.reflect.ConstantPool;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GenScan {

    public List<TxScope> getScope() {
        return new ArrayList<>();
    }

    public static void main(String[] args) throws Exception {
        Method method = Class.class.getDeclaredMethod("getConstantPool");
        method.setAccessible(true);
        ConstantPool cp = (ConstantPool) method.invoke(GenScan.class);
        for (int index = 1; index < cp.getSize(); index++) {
            try {
                Member member = cp.getMethodAt(index);
                if (GenScan.class == member.getDeclaringClass()) {
                    System.out.println(member);
                }
                System.out.println(member.getName() + " - " + member.getDeclaringClass());
            } catch (Exception e) {
            }
            try {
                System.out.println("[" + index + "] " + cp.getUTF8At(index));
            } catch (Exception ex) {

            }
        }
    }

}
