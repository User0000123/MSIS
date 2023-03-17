public class TokenEl {
        public static int counter = 1;
        public String token;
        public int count;
        public int id;

        public TokenEl(TToken tToken, int count){
            this.id = counter++;
            this.token = tToken.tokenValue();
            this.count = count;
        }

        public int getId(){
            return this.id;
        }
        public int getCount(){
            return count;
        }
        public String getToken(){
            return token;
        }
    }
