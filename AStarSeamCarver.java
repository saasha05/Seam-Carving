package seamcarving;

import astar.AStarGraph;
import astar.AStarSolver;
import astar.ShortestPathsSolver;
import astar.WeightedEdge;
import edu.princeton.cs.algs4.Picture;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class AStarSeamCarver implements SeamCarver {
    private Picture picture;

    public AStarSeamCarver(Picture picture) {
        if (picture == null) {
            throw new NullPointerException("Picture cannot be null.");
        }
        this.picture = new Picture(picture);
    }

    public Picture picture() {
        return new Picture(picture);
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public int width() {
        return picture.width();
    }

    public int height() {
        return picture.height();
    }

    public Color get(int x, int y) {
        return picture.get(x, y);
    }

    public double energy(int x, int y) {
        Color xLeft;
        Color xRight;

        if (x == 0) {
            xLeft = get(width() - 1, y);
        } else {
            xLeft = get(x - 1, y);
        }
        if (x == width() - 1) {
            xRight = get(0, y);
        } else {
            xRight = get(x + 1, y);
        }
        double xEnergy = Math.pow(xLeft.getRed() - xRight.getRed(), 2) +
                Math.pow(xLeft.getGreen() - xRight.getGreen(), 2) +
                Math.pow(xLeft.getBlue() - xRight.getBlue(), 2);
        Color yUp;
        Color yDown;
        if (y == 0) {
            yUp = get(x, height() - 1);
        } else {
            yUp = get(x, y - 1);
        }
        if (y == height() - 1) {
            yDown = get(x, 0);
        } else {
            yDown = get(x, y + 1);
        }
        double yEnergy = Math.pow(yUp.getRed() - yDown.getRed(), 2) +
                Math.pow(yUp.getGreen() - yDown.getGreen(), 2) +
                Math.pow(yUp.getBlue() - yDown.getBlue(), 2);
        return (Math.sqrt(xEnergy + yEnergy));
    }

    // 4 x 1
    // ----
    // 1 x 4
    // |
    // |
    public int[] findHorizontalSeam() {
        if (width() == 1 && height() == 1) {
            return new int[]{0};
        }
        if (height() == 1) {
            return edgeCase(width());
        }
        return postProcessSeam(false);
    }

    public int[] findVerticalSeam() {
        if (width() == 1) {
            return edgeCase(height());
        }
        return postProcessSeam(true);
    }

    private int[] edgeCase(int n) {
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = 0;
        }
        return result;
    }

    private int[] postProcessSeam(boolean isVertical) {
        SeamCarverGraph wdg = new SeamCarverGraph(picture, isVertical);
        ShortestPathsSolver<Pixel> solver = new AStarSolver<>(wdg,
                new Pixel(-1, -1), new Pixel(width(), height()), 10);
        List<Pixel> ans = solver.solution();
        if (ans.size() > 2) {
            ans.remove(ans.size() - 1);
            ans.remove(0);
        }
        int[] fin = new int[ans.size()];
        for (int i = 0; i < ans.size(); i++) {
            if (isVertical) {
                fin[i] = ans.get(i).getX();
            } else {
                fin[i] = ans.get(i).getY();
            }
            //System.out.println(fin[i]);
        }
        return fin;
    }


    private class SeamCarverGraph implements AStarGraph<Pixel> {
        private Picture picture;
        private boolean isVertical;
        public SeamCarverGraph(Picture picture, boolean isVertical) {
            this.picture = picture;
            this.isVertical = isVertical;
        }
        /** Returns the list of outgoing edges from the given vertex. */
        @Override
        public List<WeightedEdge<Pixel>> neighbors(Pixel v) {
            // weighted edge has from, to, weight
            // how do we account for orientation?
            List<WeightedEdge<Pixel>> neigh = new ArrayList<>();
            if (isVertical) {
                return neighborsVertical(v, neigh);
            } else {
                return neighborsHorizontal(v, neigh);
            }


        }

        // make less redundant
        private List<WeightedEdge<Pixel>> neighborsHorizontal(Pixel v, List<WeightedEdge<Pixel>> neigh) {
            int posRow = v.getX();
            int posCol = v.getY();
            if (posRow == -1 && posCol == -1) {
                for (int i = 0; i < height(); i++) {
                    neigh.add(new WeightedEdge(v, new Pixel(0, i), energy(0, i)));
                }
            } else if (posRow == width() - 1) {
                neigh.add(new WeightedEdge(v, new Pixel(width(), height()), 0));
                return neigh;
            } else {
                // directly right
                neigh.add(new WeightedEdge(v, new Pixel(posRow + 1, posCol), energy(posRow + 1, posCol)));
                // diagonal up
                if (posCol != 0) {
                    neigh.add(new WeightedEdge(v, new Pixel(posRow + 1, posCol - 1), energy(posRow + 1, posCol - 1)));
                }
                // diagonal down
                if (posCol != height() - 1) {
                    neigh.add(new WeightedEdge(v, new Pixel(posRow + 1, posCol + 1), energy(posRow + 1, posCol + 1)));
                }

            }
            return neigh;
        }

        private List<WeightedEdge<Pixel>> neighborsVertical(Pixel v, List<WeightedEdge<Pixel>> neigh) {
            int posRow = v.getX();
            int posCol = v.getY();
            // incorporating dummy nodes
            if (posRow == -1 && posCol == -1) {
                for (int i = 0; i < width(); i++) {
                    neigh.add(new WeightedEdge(v, new Pixel(i, 0), energy(i, 0)));
                }
            } else if (posCol == height() - 1) {
                neigh.add(new WeightedEdge(v, new Pixel(width(), height()), 0));
            } else {
                // if not bottom row
                neigh.add(new WeightedEdge(v, new Pixel(posRow, posCol + 1), energy(posRow, posCol + 1)));
                // if not first row to the left
                if (posRow != 0) {
                    neigh.add(new WeightedEdge(v, new Pixel(posRow - 1, posCol + 1), energy(posRow - 1, posCol + 1)));
                }
                // if not left most row
                if (posRow != (picture.width() - 1)) {
                    neigh.add(new WeightedEdge(v, new Pixel(posRow + 1, posCol + 1), energy(posRow + 1, posCol + 1)));
                }
            }
            return neigh;
        }

        /**
         *  Returns an estimated distance from vertex s to the goal vertex according to
         *  the A* heuristic function for this graph.
         */
        public double estimatedDistanceToGoal(Pixel s, Pixel goal) {
            return 0;
        }
    }

}


