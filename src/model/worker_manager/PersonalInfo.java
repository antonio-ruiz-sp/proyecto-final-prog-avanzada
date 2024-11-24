package model.worker_manager;

import java.util.Objects;

/**
 *
 * @author antonio.ruiz
 */
class PersonalInfo {
    private String no;
    private String sex;
    private int age;
    private String eegDate;
    private String education;
    private int IQ;
    private String mainDisorder;
    private String specificDisorder;
    private EEG firstEEG;

    public PersonalInfo(String no, String sex, int age, String eegDate, String education, int IQ, String mainDisorder, String specificDisorder, String EEGString) {
        this.no = no;
        this.sex = sex;
        this.age = age;
        this.eegDate = eegDate;
        this.education = education;
        this.IQ = IQ;
        this.mainDisorder = mainDisorder;
        this.specificDisorder = specificDisorder;
        this.firstEEG = new EEG(no,EEG.parseEEG(EEGString));
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEegDate() {
        return eegDate;
    }

    public void setEegDate(String eegDate) {
        this.eegDate = eegDate;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public int getIQ() {
        return IQ;
    }

    public void setIQ(int IQ) {
        this.IQ = IQ;
    }

    public String getMainDisorder() {
        return mainDisorder;
    }

    public void setMainDisorder(String mainDisorder) {
        this.mainDisorder = mainDisorder;
    }

    public String getSpecificDisorder() {
        return specificDisorder;
    }

    public void setSpecificDisorder(String specificDisorder) {
        this.specificDisorder = specificDisorder;
    }

    public EEG getFirstEEG() {
        return firstEEG;
    }

    public void setFirstEEG(EEG firstEEG) {
        this.firstEEG = firstEEG;
    }
    

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.no);
        hash = 89 * hash + Objects.hashCode(this.sex);
        hash = 89 * hash + this.age;
        hash = 89 * hash + Objects.hashCode(this.eegDate);
        hash = 89 * hash + Objects.hashCode(this.education);
        hash = 89 * hash + this.IQ;
        hash = 89 * hash + Objects.hashCode(this.mainDisorder);
        hash = 89 * hash + Objects.hashCode(this.specificDisorder);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PersonalInfo other = (PersonalInfo) obj;
        return this.no.equals(other.no);
    }

    @Override
    public String toString() {
        return "PersonalInfo{" + "no=" + no + ", sex=" + sex + ", age=" + age + ", eegDate=" + eegDate + ", education=" + education + ", IQ=" + IQ + ", mainDisorder=" + mainDisorder + ", specificDisorder=" + specificDisorder + '}';
    }
    
    
}
