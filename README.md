# k-means
K-menas program, ki računa na 3 načine, sekvenčno, vzporedno in porazdeljeno.

Uporaba zaenkrat:
- Zaporedno in vzporedno različico računanja se zažene iz Main.java in če se doda args (Npr. Program arguments: s2alex.txt 4 sekvencno) ob zagnu, se zažene brez GUI,če pustimo prazno se zažene z GUI, preko katerega lahko izberemo način računanja in število clusterjev (med 2 in 10).

- za porazdeljeno različico programa se zažene Porazdeljeno.java, kjer so parametri nastavljeni zaenkrat na 4 procese in 4 clusterje. Program pošlje vsem procesom vse točke za katere izračunajo centre clusterjev.


TODO/ne dela:
- Pri vzporedni in zaporedni verziji: izgleda, kot da računa samo en ali dva clusterja in to naključno
- Pri porazdeljeni, ne zapisuje v receiveBuffer, zato na koncu vrne prazen New_receiveBuffer.
