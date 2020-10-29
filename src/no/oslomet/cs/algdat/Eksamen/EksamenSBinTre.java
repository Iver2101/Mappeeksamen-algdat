package no.oslomet.cs.algdat.Eksamen;

import java.util.*;

public class EksamenSBinTre<T> {
    private static final class Node<T>   // en indre nodeklasse
    {
        private T verdi;                   // nodens verdi
        private Node<T> venstre, høyre;    // venstre og høyre barn
        private Node<T> forelder;          // forelder

        // konstruktør
        private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder) {
            this.verdi = verdi;
            venstre = v;
            høyre = h;
            this.forelder = forelder;
        }

        private Node(T verdi, Node<T> forelder)  // konstruktør
        {
            this(verdi, null, null, forelder);
        }

        @Override
        public String toString() {
            return "" + verdi;
        }

    } // class Node

    private Node<T> rot;                            // peker til rotnoden
    private int antall;                             // antall noder
    private int endringer;                          // antall endringer

    private final Comparator<? super T> comp;       // komparator

    public EksamenSBinTre(Comparator<? super T> c)    // konstruktør
    {
        rot = null;
        antall = 0;
        comp = c;
    }

    public boolean inneholder(T verdi) {
        if (verdi == null) return false;

        Node<T> p = rot;

        while (p != null) {
            int cmp = comp.compare(verdi, p.verdi);
            if (cmp < 0) p = p.venstre;
            else if (cmp > 0) p = p.høyre;
            else return true;
        }

        return false;
    }

    public int antall() {
        return antall;
    }

    public String toStringPostOrder() {
        if (tom()) return "[]";

        StringJoiner s = new StringJoiner(", ", "[", "]");

        Node<T> p = førstePostorden(rot); // går til den første i postorden
        while (p != null) {
            s.add(p.verdi.toString());
            p = nestePostorden(p);
        }

        return s.toString();
    }

    public boolean tom() {
        return antall == 0;
    }

    public final boolean leggInn(T verdi)    // skal ligge i class SBinTre
    {
        Objects.requireNonNull(verdi, "Ulovlig med nullverdier!");

        Node<T> p = rot, q = null;               // p starter i roten
        int cmp = 0;                             // hjelpevariabel

        while (p != null)       // fortsetter til p er ute av treet
        {
            q = p;                                 // q er forelder til p
            cmp = comp.compare(verdi,p.verdi);     // bruker komparatoren
            p = cmp < 0 ? p.venstre : p.høyre;     // flytter p
        }
        // p er nå null, dvs. ute av treet, q er den siste vi passerte
        p = new Node<>(verdi, q);// oppretter en ny node


        if (q == null) rot = p;                  // p blir rotnode
        else if (cmp < 0) q.venstre = p;         // venstre barn til q
        else q.høyre = p;                        // høyre barn til q
        antall++;
        endringer++;
        return true;                             // vellykket innlegging
    }

    public boolean fjern(T verdi) {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    public int fjernAlle(T verdi) {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    public int antall(T verdi) {
        if(!inneholder(verdi) || tom()) {  //sjekker om treet ikke inneholder verdien og om det er tomt. Returnerter 0 viss noe av dette stemmer.
            return 0;
        }
        int tall = 0;

        ArrayDeque<Node<T>> kø = new ArrayDeque<>(); //lager kø som skal brukes til å travasere det binæretreet
        kø.addLast(rot);                             //legger til rota som siste og eneste element i køen.

        while (!kø.isEmpty()) {
            Node<T> element = kø.removeFirst();      //fjerner siste element i køen

            if(element.venstre != null) {
                kø.addLast(element.venstre);         //legger til venstrebarn til elementet som ble fjernet viss det eksisterer
            }

            if(element.høyre != null) {              //samme som if-setningen over, bare for høyrebarn.
                kø.addLast(element.høyre);
            }

            if(element.verdi == verdi) {            //sjekker om verdien til det fjerna elementet matcher verdi du leter etter. legger til 1 til return verdien viss.
                tall++;
            }
        }
        return tall;                                //returnerer antall ganger verdien oppstår.
     }

    public void nullstill() {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    private static <T> Node<T> førstePostorden(Node<T> p) {
        Node<T> q = p;
            while (true)
            {
                if (q.venstre != null) q = q.venstre;       //Looper gjennom treet helt til
                else if (q.høyre != null) q = q.høyre;      //vi finner noden nederst til venstre.
                else return q;                              //returnerer noden
            }

    }

    private static <T> Node<T> nestePostorden(Node<T> p) {
            if(p.forelder == null) return null;                 //returnerer null viss p er den siste i postorden

        Node<T> forelder = p.forelder;
        if(forelder.høyre == p || forelder.høyre == null) return forelder;    //returnerer foreldrenoden til p viss p er høyre barn til foreldrenoden eller enebarn.

        return førstePostorden(forelder.høyre);                 //returnerer første verdi i postorden fra det høyre subtreet til foreldre noden til p

    }

    public void postorden(Oppgave<? super T> oppgave) {
        Node<T> nod = førstePostorden(rot);
        while(nod != null) {
            oppgave.utførOppgave(nod.verdi);    //Travaserer gjennom hele treet i postorden og utfører oppgave på hvert element.
            nod = nestePostorden(nod);
        }
    }

    public void postordenRecursive(Oppgave<? super T> oppgave) {
        postordenRecursive(rot, oppgave);
    }

    private void postordenRecursive(Node<T> p, Oppgave<? super T> oppgave) {

        if(p == null) return; // returnerer viss p er tom

        postordenRecursive(p.venstre,oppgave);    //traverserer gjennom venstre subtre til p

        postordenRecursive(p.høyre, oppgave);     //traverserer gjennom høyre subtre til p

        oppgave.utførOppgave(p.verdi);          //utfører oppgaven

    }

    public ArrayList<T> serialize() {

        ArrayList<T> ut = new ArrayList<>(); //arrayet som skal returners blir initialisert
        if(tom()) return ut;
        ArrayDeque<Node<T>> kø = new ArrayDeque<>(); //lager kø som brukes for å travarsere arrayet
        kø.add(rot);
        while (!kø.isEmpty()) {
            Node<T> curr = kø.removeFirst();
            if(curr.venstre != null) kø.add(curr.venstre);
            if(curr.høyre != null) kø.add(curr.høyre);
            ut.add(curr.verdi);
        }
        return ut;


    }

    static <K> EksamenSBinTre<K> deserialize(ArrayList<K> data, Comparator<? super K> c) {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }


} // ObligSBinTre
