package com.edufox.schoolerp.canteen;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CanteenService {
    private final CanteenRepository repo;
    private final DatabaseWriteGuard writeGuard;

    public Map<String,Object> bootstrap(String school,String session,String staff){return repo.bootstrap(school,session,staff);}
    public List<Map<String,Object>> groups(String school){return repo.expenseGroups(school);}
    @Transactional public Map<String,Object> saveGroup(String id,Map<String,Object>b,String school,String staff){writeGuard.requireWritable();return repo.saveExpenseGroup(id,b,school,staff,!id.isBlank());}
    @Transactional public Map<String,Object> deleteGroup(String id,String school){writeGuard.requireWritable();repo.deleteExpenseGroup(id,school);return msg("Record deleted successfully");}
    public List<Map<String,Object>> names(String school){return repo.expenseNames(school);}
    @Transactional public Map<String,Object> saveName(String id,Map<String,Object>b,String school,String staff){writeGuard.requireWritable();return repo.saveExpenseName(id,b,school,staff,!id.isBlank());}
    @Transactional public Map<String,Object> deleteName(String id,String school){writeGuard.requireWritable();repo.deleteExpenseName(id,school);return msg("Record deleted successfully");}
    public List<Map<String,Object>> productGroups(String school){return repo.productGroups(school);}
    @Transactional public Map<String,Object> saveProductGroup(String id,Map<String,Object>b,String school,String staff){writeGuard.requireWritable();return repo.saveProductGroup(id,b,school,staff,!id.isBlank());}
    @Transactional public Map<String,Object> deleteProductGroup(String id,String school){writeGuard.requireWritable();repo.deleteProductGroup(id,school);return msg("Record deleted successfully");}
    public List<Map<String,Object>> products(String school,String session){return repo.products(school,session);}
    @Transactional public Map<String,Object> saveProduct(String id,Map<String,Object>b,String school,String session,String staff){writeGuard.requireWritable();return repo.saveProduct(id,b,school,session,staff,!id.isBlank());}
    @Transactional public Map<String,Object> deleteProduct(String id,String school){writeGuard.requireWritable();repo.deleteProduct(id,school);return msg("Record deleted successfully");}
    public List<Map<String,Object>> credits(String school,String session){return repo.creditBalances(school,session);}
    @Transactional public Map<String,Object> updateCredit(String reg,String session,Map<String,Object>b,String school,String staff){writeGuard.requireWritable();repo.updateCreditBalance(reg,session,b.get("creditBalance"),school,staff);return msg("Record Updated Sucessfully");}
    public List<Map<String,Object>> students(String school,String session,String sec){return repo.students(school,session,sec);}
    public Map<String,Object> student(String school,String session,String reg){return repo.studentBalance(school,session,reg);}
    public List<Map<String,Object>> suggestions(String school,String session,String prefix){return repo.registrationSuggestions(school,session,prefix);}
    public Map<String,Object> history(String school,String session,String reg){return map("rows",repo.expenseHistory(school,session,reg),"summary",repo.expenseSummary(school,session,reg));}
    @Transactional public Map<String,Object> saveAssignments(String school,String session,String staff,Map<String,Object>b){writeGuard.requireWritable();repo.saveAssignments(school,session,String.valueOf(b.getOrDefault("registrationNo","")),String.valueOf(b.getOrDefault("transDate","")),(List<Map<String,Object>>)b.getOrDefault("rows",List.of()),staff);return msg("Record Saved Successfully");}
    @Transactional public Map<String,Object> updateAssignment(String id,String school,String session,String reg,Map<String,Object>b){writeGuard.requireWritable();repo.updateAssignment(id,school,session,reg,b);return msg("Record Updated Sucessfully");}
    @Transactional public Map<String,Object> deleteAssignment(String id,String school,String session,String reg){writeGuard.requireWritable();repo.deleteAssignment(id,school,session,reg);return msg("Record Deleted Sucessfully");}
    @Transactional public Map<String,Object> debitCredit(String school,String session,String classId,String secId){return map("rows",repo.debitCreditReport(school,session,classId,secId));}
    public Map<String,Object> invoice(String school,String session,String reg,String classId,String secId){return repo.invoice(school,session,reg,classId,secId);}
    public List<Map<String,Object>> productBalance(String school,String session){return repo.productBalance(school,session);}
    public List<Map<String,Object>> remaining(String school,String session){return repo.products(school,session);}
    public List<Map<String,Object>> assignReport(String school,String session){return repo.assignExpenseReport(school,session);}
    public List<Map<String,Object>> issues(String school,String session,String type,String reg){return repo.productIssues(school,session,type,reg);}
    public List<Map<String,Object>> productReport(String school,String session,String type){return repo.productReport(school,session,type);}
    private static Map<String,Object> msg(String message){return map("message",message);}
    private static Map<String,Object> map(Object...v){Map<String,Object>m=new LinkedHashMap<>();for(int i=0;i+1<v.length;i+=2)m.put(String.valueOf(v[i]),v[i+1]);return m;}
}
