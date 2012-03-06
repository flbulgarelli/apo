package com.uqbar.poo.swing;
import javax.swing.JFrame;

    public class PersonBean {
        private String firstName;
        private String lastName;

        public PersonBean(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String toString() {
            return getFirstName() + " " + getLastName();
        }

    public static void main(String[] a){
      JFrame f = new JFrame("Presentation PropertyChange Example");
      f.setDefaultCloseOperation(2);
      f.pack();
      f.setVisible(true);
    }
}