public class TestCode {

    public static class Amiddle {
        public void abc() { System.out.println("Amiddle.abc()"); }
        public void ab() { System.out.println("Amiddle.ac()"); }
        public void ac() { System.out.println("Amiddle.ac()"); }
    }

    public static class Bleft extends Amiddle {
        public void abc() { System.out.println("Bleft.abc()"); }
        public void ab() { System.out.println("Bleft.ac()"); }
        public void bc() { System.out.println("Bleft.bc()"); }
    }

    public static class Cleft extends Bleft {
        public void abc() { System.out.println("Cleft.abc()"); }
        public void ac() { System.out.println("Cleft.ac()"); }
        public void bc() { System.out.println("Cleft.bc()"); }
    }

    public static class Bright extends Amiddle {
        private Amiddle test=new Amiddle();
        public void abc() { System.out.println("Bright.abc()"); }
        public void ab() { System.out.println("Bright.ac()"); }
        public void bc() { System.out.println("Bright.bc()"); }
    }

    public static class Cright extends Bright {
        public Bright maaz=new Bright();
        public void abc() { System.out.println("Cright.abc()"); }
        public void ac() { System.out.println("Cright.ac()"); }
        public void bc() { System.out.println("Cright.bc()"); }
    }

}
