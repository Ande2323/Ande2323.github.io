# Ande2323.github.io

E-portafolio del curso **Bases de Datos 1**. Sitio estático, sin dependencias
ni proceso de build: lo que está en la rama `main` es exactamente lo que se
publica.

**Sitio:** https://ande2323.github.io

## Estructura

```
index.html        Todo el contenido del sitio
styles.css        Estilos
assets/           Imágenes (avatar, foto de perfil)
evidencias/       Archivos de los trabajos, una carpeta por categoría
GUION-VIDEO.md    Guion y pasos para grabar y subir el video
.nojekyll         Evita que GitHub Pages procese el sitio con Jekyll
```

## Cómo editar

Todo el texto que hay que personalizar está marcado en `index.html` con un
comentario `<!-- EDITA: ... -->`. Busca esa palabra y vas a encontrar los
cuatro lugares que importan.

### Cambiar la foto

1. Copia tu foto a `assets/` con el nombre `foto.jpg`.
2. En `index.html`, dentro de `<figure class="hero__photo">`, cambia
   `src="assets/avatar.svg"` por `src="assets/foto.jpg"`.

Sale cuadrada, así que una foto más o menos centrada funciona mejor.

### Insertar el video

En `index.html`, en la sección Presentación, hay un bloque comentado con el
`<iframe>` listo. Sigue las instrucciones que están ahí mismo: borra el
`<div class="videobox">` y descomenta el iframe con el ID de tu video.

### Agregar una evidencia

1. Copia los archivos a `evidencias/<categoría>/<nombre-del-trabajo>/`.
2. En `index.html`, dentro del `<div class="evgroup">` que corresponda, duplica
   un `<article class="ev">` y cambia la fecha, el título, la descripción y la
   lista de `<li>` dentro de `<ul class="files">`.
3. Sube el contador de la categoría en su `<a class="chip">`.

Para una categoría que aún está vacía (`SQL`, `Proyecto`), reemplaza el
`<p class="evgroup__vacio">` por los `<article class="ev">` correspondientes.

**No publiques credenciales.** Nada de wallets de Oracle, `tnsnames.ora`,
archivos `.properties` con contraseñas ni cadenas de conexión: este repositorio
es público.

### Convertir un documento Markdown en página web

Las evidencias en `.md` se publican como página con estilos propios. El script
que generó `evidencias/modelado/taller-eer-puerto/index.html` usa
[marked](https://www.npmjs.com/package/marked); los estilos ya están en
`styles.css` bajo la clase `.doc`.

## Ver el sitio en local

```
python -m http.server 4321
```

Y abre http://localhost:4321

## Publicar cambios

```
git add .
git commit -m "Descripción del cambio"
git push
```

GitHub Pages republica solo, en menos de un minuto.
