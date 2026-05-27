package DB2026Team02.model;

public class Professor {

    private int professorId;
    private int departmentId;
    private String professorName;
    private String email;
    private String phone;

    public Professor() {
    }

    public Professor(int departmentId, String professorName, String email, String phone) {
        this.departmentId = departmentId;
        this.professorName = professorName;
        this.email = email;
        this.phone = phone;
    }

    public int getProfessorId() {
        return professorId;
    }

    public void setProfessorId(int professorId) {
        this.professorId = professorId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
