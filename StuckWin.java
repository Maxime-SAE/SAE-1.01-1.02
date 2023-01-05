import java.lang.Thread.State;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StuckWin {
  static final Scanner input = new Scanner(System.in);
  private static final double BOARD_SIZE = 7;
  static final char[] lettres = {'A', 'B', 'C', 'D', 'E', 'F', 'G'};// on initialise toute les lettres possibles pour le jeu 

  enum Result {
    OK, BAD_COLOR, DEST_NOT_FREE, EMPTY_SRC, TOO_FAR, EXT_BOARD, EXIT
  }

  enum ModeMvt {
    REAL, SIMU
  }

  final char[] joueurs = { 'B', 'R' };
  final int SIZE = 8;
  final char VIDE = '.';
  // 'B'=bleu 'R'=rouge '.'=vide '-'=n'existe pas
  char[][] state = {
      { '-', '-', '-', '-', 'R', 'R', 'R', 'R' },
      { '-', '-', '-', '.', 'R', 'R', 'R', 'R' },
      { '-', '-', '.', '.', '.', 'R', 'R', 'R' },
      { '-', 'B', 'B', '.', '.', '.', 'R', 'R' },
      { '-', 'B', 'B', 'B', '.', '.', '.', '-' },
      { '-', 'B', 'B', 'B', 'B', '.', '-', '-' },
      { '-', 'B', 'B', 'B', 'B', '-', '-', '-' },
  };
  char[][] stateInvers = ReversiState(state.clone());

  /**
   * Déplace un pion ou simule son déplacement
   * 
   * @param couleur  couleur du pion à déplacer
   * @param lcSource case source Lc
   * @param lcDest   case destination Lc
   * @param mode     ModeMVT.REAL/SIMU selon qu'on réalise effectivement le
   *                 déplacement ou qu'on le simule seulement.
   * @return enum {OK, BAD_COLOR, DEST_NOT_FREE, EMPTY_SRC, TOO_FAR, EXT_BOARD,
   *         EXIT} selon le déplacement
   */
  Result deplace(char couleur, String lcSource, String lcDest, ModeMvt mode) {

    if(state[getIdLettre(lcSource.charAt(0))][Integer.parseInt(lcDest.replaceAll("[^0-7]", ""))] == '-' ){
      return Result.EXT_BOARD;
    }
    return Result.OK;

  }
  

  int getIdLettre(char lettre) {
    int result = -1;
    for(int i = 0; i < lettres.length; i++) {
      if(lettres[i] == lettre) {
        result = i;
        break;
      }
    }
    return result;

    
  }

  

  /**
   * Construit les trois chaînes représentant les positions accessibles
   * à partir de la position de départ [idLettre][idCol].
   * 
   * @param couleur  couleur du pion à jouer
   * @param idLettre id de la ligne du pion à jouer
   * @param idCol    id de la colonne du pion à jouer
   * @return tableau des trois positions jouables par le pion (redondance possible
   *         sur les bords)
   */
  String[] possibleDests(char couleur, int idLettre, int idCol){
    String[] destinations = new String[3];
    int index = 0;
    
    switch (couleur) {
      case 'B':
        for(int i = idLettre-1; i < idLettre + 1; i++) {
          for(int j = idCol; j < idCol + 2; j++) {
            if(idLettre >= 0 && idLettre < BOARD_SIZE && idCol >= 0 && idCol < SIZE) { /*Tester si idLettre et idCol sont dans le tableau pour les Bleus*/
              destinations[index] = "" + lettres[idLettre] + idCol;
            }
            if(!(i == idLettre && j == idCol)){
              index++;
            }
          }
        }
        break;
      case 'R':
      for(int i = idLettre; i < idLettre + 2; i++) {
        for(int j = idCol - 1; j < idCol + 1; j++) {
          if(idLettre >= 0 && idLettre < BOARD_SIZE && idCol >= 0 && idCol < SIZE) {/*Tester si idLettre et idCol sont dans le tableau pour les Rouges */
            destinations[index] = "" + lettres[idLettre] + idCol;
          }
          if(!(i == idLettre && j == idCol)){
            index++;
          }
        }
      }
        break;
      default:
        break;
    }

    return destinations;
  }
/**
 * on clone le tableau pour garder l'initial
 * @param stateClone
 * @return le tableau inverser
 */
 
  char[][] ReversiState(char[][] stateClone) {
    char temp;

    for(int i = 0; i < stateClone.length; i++) {
        for(int j = 0; j < stateClone[i].length/2; j++) {
            temp = stateClone[i][j];
            stateClone[i][j] = stateClone[i][stateClone[i].length - j - 1];
            stateClone[i][stateClone[i].length - j - 1] = temp;
        }
    }

    return state;
 }
  /**
   * Affiche le plateau de jeu dans le terminal avec l'attribut d'état "stateInvers"
   * on rajoute les coulrus pour différencier les équipes
   * on apppel la fonction Lettres pour avoir les cases correpondant
   */

 void affiche() {
    int spaceCount = 4;
    String line = "";

    for( int k = 0 ; k <= stateInvers.length + stateInvers[0].length - 2; k++ ) {
        for( int j = 0 ; j <= k ; j++ ) {
            int i = k - j;
            if( i < stateInvers.length && j < stateInvers[0].length ) {
                if(stateInvers[i][j] != '-' ) {
                    if(stateInvers[i][j] == 'B') {
                      line = line + ConsoleColors.BLUE_BACKGROUND;
                    }else if(stateInvers[i][j] == 'R') {
                      line = line + ConsoleColors.RED_BACKGROUND;
                    }else {
                      line = line + ConsoleColors.WHITE_BACKGROUND;
                    }
                    line = line + lettres[j] + (7 - i)  + ConsoleColors.RESET + "  ";
                    spaceCount--;
                }
            }
        }
        for (int i = 0; i < spaceCount; i++) {
            System.out.print("  ");
        }
        System.out.println(line);
        line = "";
        spaceCount = 4;
    }
 }  

  // votre code ici

  /**
   * Joue un tour
   * 
   * @param couleur couleur du pion à jouer
   * @return tableau contenant la position de départ et la destination du pion à
   *         jouer.
   */
  String[] jouerIA(char couleur) {
    // votre code ici. Supprimer la ligne ci-dessous.
    throw new java.lang.UnsupportedOperationException("à compléter");
  }

  /**
   * gère le jeu en fonction du joueur/couleur
   * 
   * @param couleur
   * @return tableau de deux chaînes {source,destination} du pion à jouer
   */
  String[] jouer(char couleur) {
    String src = "";
    String dst = "";
    String[] mvtIa;
    switch (couleur) {
      case 'B':
        System.out.println("Mouvement " + couleur);
        src = input.next();
        dst = input.next();
        System.out.println(src + "->" + dst);
        break;
      case 'R':
        System.out.println("Mouvement " + couleur);
        //mvtIa = jouerIA(couleur);
        src = input.next();
        dst = input.next();
        System.out.println(src + "->" + dst);
        break;
    }
    return new String[] { src, dst };
  }

  /**
   * retourne la couleur gagnante et si la partie se finit par un égalité, on renvoie 'N'
   * @param couleur définit la couleur du joueur conscernée
   * @return retourne la couleur gagnante
   */
  char finPartie(char couleur){
    for (int i = 0; i < state.length; i++) {
      for (int j = 0; j < state[i].length; j++) {
        if (state[i][j] == couleur) {
          String[] destinations = possibleDests(couleur, i, j);
          for (int k = 0; k < destinations.length; k++) {
            if(destinations[k] != "") {
              couleur = 'N';
            }
          }
        }
      }
    }

    return couleur;
  }

  /**
   * 
   * @param args
   */
  public static void main(String[] args) {
    StuckWin jeu = new StuckWin();
    String src = "";
    String dest;
    String[] reponse;
    Result status;

    char partie = 'N';
    char curCouleur = jeu.joueurs[0];
    char nextCouleur = jeu.joueurs[1];
    char tmp;
    int cpt = 0;

    // version console
    do {
      jeu.affiche();

      do {
        status = Result.EXIT;
        reponse = jeu.jouer(curCouleur);
        src = reponse[0];
        dest = reponse[1];
        if ("q".equals(src))
          return;
        status = jeu.deplace(curCouleur, src, dest, ModeMvt.REAL);
        partie = jeu.finPartie(nextCouleur);
        System.out.println("status : " + status + " partie : " + partie);
      } while (status != Result.OK && partie == 'N');
      tmp = curCouleur;
      curCouleur = nextCouleur;
      nextCouleur = tmp;
      cpt++;
    } while (partie == 'N'); // TODO affiche vainqueur
    System.out.printf("Victoire : " + partie + " (" + (cpt / 2) + " coups)");
  }
}