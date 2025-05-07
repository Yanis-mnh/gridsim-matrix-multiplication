package gridPorject;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Matrice {
    private final ArrayList<ArrayList<Float>> matrice;
    private  int nbrLignes;
    private  int nbrColonnes;
    private String name;

    public Matrice( ) {
    	this.name = "";
    	this.matrice = new ArrayList<ArrayList<Float>>();
    }
    public Matrice(String name) {
    	this.name = name;
        System.out.println("---------- Donnees de la MATRICE "+ name+" ---------");
    	Scanner scanner = new Scanner(System.in);
        
        // Lire le nombre de lignes
        while (true) {
            try {
                System.out.print("➡ Entrez le nombre de lignes : ");
                nbrLignes = scanner.nextInt();
                if (nbrLignes <= 0) throw new Exception();
                break;
            } catch (Exception e) {
                System.out.println("Veuillez entrer un entier positif.");
                scanner.nextLine();
            }
        }

        // Lire le nombre de colonnes
        while (true) {
            try {
                System.out.print("➡ Entrez le nombre de colonnes : ");
                nbrColonnes = scanner.nextInt();
                if (nbrColonnes <= 0) throw new Exception();
                break;
            } catch (Exception e) {
                System.out.println("Veuillez entrer un entier positif.");
                scanner.nextLine(); // vider le buffer
            }
        }

        matrice = new ArrayList<>();
        System.out.println("\nRemplissage de la matrice :");

        for (int i = 0; i < nbrLignes; i++) {
            ArrayList<Float> ligne = new ArrayList<>();
            for (int j = 0; j < nbrColonnes; j++) {
                while (true) {
                    try {
                        System.out.print("Valeur [" + (i+1) + "," + (j+1) + "] : ");
                        float val = scanner.nextFloat();
                        ligne.add(val);
                        break;
                    } catch (InputMismatchException e) {
                        System.out.println("Veuillez entrer un nombre valide (ex : 3.14).");
                        scanner.nextLine(); // vider le buffer
                    }
                }
            }
            matrice.add(ligne);
        }
    }
    
    public Matrice (String name,ArrayList<ArrayList<Float>> matrice) {
    	this.name = name;
    	this.matrice = matrice;
        this.nbrLignes = matrice.size();
        this.nbrColonnes = matrice.get(0).size();
    }

    public ArrayList<Float> getLigne(int ligne) {
        if (ligne < 0 || ligne >= nbrLignes)
            throw new IndexOutOfBoundsException("Ligne invalide");
        return new ArrayList<>(matrice.get(ligne));
    }

    public ArrayList<Float> getColonne(int colonne) {
        if (colonne < 0 || colonne >= nbrColonnes) {
            throw new IndexOutOfBoundsException("Colonne invalide : " + colonne);
        }

        return matrice.stream()
                      .map(ligne -> ligne.get(colonne))
                      .collect(Collectors.toCollection(ArrayList::new));
    }
    
    public boolean canMultiply(Matrice autre) {
        return this.nbrColonnes == autre.nbrLignes;
    }
    public int getNbrLigne() {
		return nbrLignes;
	}
    public int getNbrCol() {
		return nbrColonnes;
	}
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========== MATRICE "+ this.name+" ===========\n");
        for (ArrayList<Float> ligne : matrice) {
            for (float val : ligne) {
                sb.append(String.format("%.2f\t", val));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
