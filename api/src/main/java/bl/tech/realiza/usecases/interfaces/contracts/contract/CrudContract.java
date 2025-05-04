package bl.tech.realiza.usecases.interfaces.contracts.contract;

public interface CrudContract {
    String finishContract(String idContract);
    String addEmployeeToContract(String idContract, String idEmployee);
    String removeEmployeeToContract(String idContract, String idEmployee);
}
