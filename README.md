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

Cada categoría es un `<li class="card">` en la sección Evidencias. Para
publicar un entregable, reemplaza el `<span class="pill">Próximamente</span>`
por un enlace al archivo o al repositorio correspondiente.

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
