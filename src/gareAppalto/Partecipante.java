package gareAppalto;

public class Partecipante extends  Thread{
    private String id;
    private Offerta offerta;
    private static final int nPort=4000; //ricezione connessioni partecipanti

    public Partecipante(String id){
        this.id=id;
        this.offerta=new Offerta((int)Math.random()*100,id);
    }

    @Override
    public void run() {

    }
}
