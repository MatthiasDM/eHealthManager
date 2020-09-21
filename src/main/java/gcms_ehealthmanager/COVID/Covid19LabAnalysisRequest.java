/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gcms_ehealthmanager.COVID;

/**
 *
 * @author Matthias
 */
public class Covid19LabAnalysisRequest {

    private Covid19labanalysisrequest covid19labanalysisrequest;

    public Covid19labanalysisrequest getCovid19labanalysisrequest() {
        return covid19labanalysisrequest;
    }

    public void setCovid19labanalysisrequest(Covid19labanalysisrequest covid19labanalysisrequest) {
        this.covid19labanalysisrequest = covid19labanalysisrequest;
    }

    @Override
    public String toString() {
        return "ClassPojo [covid19labanalysisrequest = " + covid19labanalysisrequest + "]";
    }

    class Covid19labanalysisrequest {

        private Prescriber prescriber;

        private Patient patient;

        private String forwardresultsto;

        private Clinicaldata clinicaldata;

        public Prescriber getPrescriber() {
            return prescriber;
        }

        public void setPrescriber(Prescriber prescriber) {
            this.prescriber = prescriber;
        }

        public Patient getPatient() {
            return patient;
        }

        public void setPatient(Patient patient) {
            this.patient = patient;
        }

        public String getForwardresultsto() {
            return forwardresultsto;
        }

        public void setForwardresultsto(String forwardresultsto) {
            this.forwardresultsto = forwardresultsto;
        }

        public Clinicaldata getClinicaldata() {
            return clinicaldata;
        }

        public void setClinicaldata(Clinicaldata clinicaldata) {
            this.clinicaldata = clinicaldata;
        }

        @Override
        public String toString() {
            return "ClassPojo [prescriber = " + prescriber + ", patient = " + patient + ", forwardresultsto = " + forwardresultsto + ", clinicaldata = " + clinicaldata + "]";
        }
    }

    class Clinicaldata {

        private String casedefinitioncompliance;

        private String symptomssince;

        private String testprescribedreason;

        private String hospitalisation;

        private String illnesscollection;

        private Samples samples;

        public String getCasedefinitioncompliance() {
            return casedefinitioncompliance;
        }

        public void setCasedefinitioncompliance(String casedefinitioncompliance) {
            this.casedefinitioncompliance = casedefinitioncompliance;
        }

        public String getSymptomssince() {
            return symptomssince;
        }

        public void setSymptomssince(String symptomssince) {
            this.symptomssince = symptomssince;
        }

        public String getTestprescribedreason() {
            return testprescribedreason;
        }

        public void setTestprescribedreason(String testprescribedreason) {
            this.testprescribedreason = testprescribedreason;
        }

        public String getHospitalisation() {
            return hospitalisation;
        }

        public void setHospitalisation(String hospitalisation) {
            this.hospitalisation = hospitalisation;
        }

        public String getIllnesscollection() {
            return illnesscollection;
        }

        public void setIllnesscollection(String illnesscollection) {
            this.illnesscollection = illnesscollection;
        }

        public Samples getSamples() {
            return samples;
        }

        public void setSamples(Samples samples) {
            this.samples = samples;
        }

        @Override
        public String toString() {
            return "ClassPojo [casedefinitioncompliance = " + casedefinitioncompliance + ", symptomssince = " + symptomssince + ", testprescribedreason = " + testprescribedreason + ", hospitalisation = " + hospitalisation + ", illnesscollection = " + illnesscollection + ", samples = " + samples + "]";
        }
    }

    class Samples {

        private Sample sample;

        public Sample getSample() {
            return sample;
        }

        public void setSample(Sample sample) {
            this.sample = sample;
        }

        @Override
        public String toString() {
            return "ClassPojo [sample = " + sample + "]";
        }
    }

    class Sample {

        private Tests tests;

        private String time;

        private String type;

        private String identificationnumber;

        public Tests getTests() {
            return tests;
        }

        public void setTests(Tests tests) {
            this.tests = tests;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIdentificationnumber() {
            return identificationnumber;
        }

        public void setIdentificationnumber(String identificationnumber) {
            this.identificationnumber = identificationnumber;
        }

        @Override
        public String toString() {
            return "ClassPojo [tests = " + tests + ", time = " + time + ", type = " + type + ", identificationnumber = " + identificationnumber + "]";
        }
    }

    class Tests {

        private String test;

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        @Override
        public String toString() {
            return "ClassPojo [test = " + test + "]";
        }
    }

    class Patient {

        private String dateofbirth;

        private String firstname;

        private String housenumberofresidence;

        private String ssin;

        private String countryofresidence;

        private String gender;

        private String postalcodeofresidence;

        private String streetofresidence;

        private String cityofresidence;

        private String boxnumberofresidence;

        private String lastname;

        public String getDateofbirth() {
            return dateofbirth;
        }

        public void setDateofbirth(String dateofbirth) {
            this.dateofbirth = dateofbirth;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getHousenumberofresidence() {
            return housenumberofresidence;
        }

        public void setHousenumberofresidence(String housenumberofresidence) {
            this.housenumberofresidence = housenumberofresidence;
        }

        public String getSsin() {
            return ssin;
        }

        public void setSsin(String ssin) {
            this.ssin = ssin;
        }

        public String getCountryofresidence() {
            return countryofresidence;
        }

        public void setCountryofresidence(String countryofresidence) {
            this.countryofresidence = countryofresidence;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getPostalcodeofresidence() {
            return postalcodeofresidence;
        }

        public void setPostalcodeofresidence(String postalcodeofresidence) {
            this.postalcodeofresidence = postalcodeofresidence;
        }

        public String getStreetofresidence() {
            return streetofresidence;
        }

        public void setStreetofresidence(String streetofresidence) {
            this.streetofresidence = streetofresidence;
        }

        public String getCityofresidence() {
            return cityofresidence;
        }

        public void setCityofresidence(String cityofresidence) {
            this.cityofresidence = cityofresidence;
        }

        public String getBoxnumberofresidence() {
            return boxnumberofresidence;
        }

        public void setBoxnumberofresidence(String boxnumberofresidence) {
            this.boxnumberofresidence = boxnumberofresidence;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        @Override
        public String toString() {
            return "ClassPojo [dateofbirth = " + dateofbirth + ", firstname = " + firstname + ", housenumberofresidence = " + housenumberofresidence + ", ssin = " + ssin + ", countryofresidence = " + countryofresidence + ", gender = " + gender + ", postalcodeofresidence = " + postalcodeofresidence + ", streetofresidence = " + streetofresidence + ", cityofresidence = " + cityofresidence + ", boxnumberofresidence = " + boxnumberofresidence + ", lastname = " + lastname + "]";
        }
    }

    class Prescriber {

        private String firstname;

        private String nihii;

        private String lastname;

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getNihii() {
            return nihii;
        }

        public void setNihii(String nihii) {
            this.nihii = nihii;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        @Override
        public String toString() {
            return "ClassPojo [firstname = " + firstname + ", nihii = " + nihii + ", lastname = " + lastname + "]";
        }
    }

}
