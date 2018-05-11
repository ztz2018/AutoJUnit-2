package sample_project;

public class MainClass {

    public static void main(String []args) {

        // This is a sample java project

        ModelDTO model = new ModelDTO();
        model.setId(1);
        model.setName("SomeName");
        model.setType("SomeType");

        System.out.println("Model created is : " + model.toString());

    }

}
