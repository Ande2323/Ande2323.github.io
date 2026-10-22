# Modelo Entidad-Relación Extendido — Dominio Portuario

**Universidad El Bosque** · Bases de Datos 1
Documento de apoyo al diagrama `ER_Puerto_Chen.drawio`

---

## 1. Entidades y atributos

| Entidad | Qué representa |
|---|---|
| `IMPORTADOR` | La empresa o persona dueña de la carga |
| `SERVICIO` | La ruta marítima comercial (ej. Cartagena–Shanghái), independiente de la fecha |
| `RECALADA` | La llegada concreta de un servicio al puerto en una fecha |
| `BUQUE` | La nave física, identificada por su número IMO |
| `CERTIFICADO` | El certificado de clasificación vigente del buque |
| `OPERARIO` | Trabajador portuario |
| `SUMINISTRO` | Artículo entregado al buque durante la escala |
| `CONTRATISTA` | Empresa que entrega los suministros |
| `BODEGA` | Espacio del patio donde el puerto almacena la carga de un embarque |

### 1.1 `IMPORTADOR`

| Atributo | Tipo | Notación |
|---|---|---|
| `documento` | Identificador | Elipse subrayada |
| `nombre_completo` | **Compuesto** → `nombres`, `apellidos` | Elipse con elipses hijas |
| `fecha_registro` | Simple | Elipse |
| `correo` | **Multivaluado** | Elipse doble |
| `antiguedad_cliente` | **Derivado** de `fecha_registro` | Elipse punteada |

### 1.2 `SERVICIO`

| Atributo | Tipo |
|---|---|
| `codigo_servicio` | Identificador |
| `puerto_origen` | Simple |
| `puerto_destino` | Simple |
| `transito_estimado` | Simple |

### 1.3 `RECALADA` — entidad débil

| Atributo | Tipo |
|---|---|
| `fecha_arribo` | **Clave parcial** (discriminante) — subrayado punteado |
| `hora_arribo` | Simple |
| `estado` | Simple |

Depende en identificación de `SERVICIO`. Su identificación completa es `codigo_servicio` + `fecha_arribo`.

### 1.4 `BUQUE`

| Atributo | Tipo |
|---|---|
| `imo` | Identificador |
| `nombre_buque` | Simple |
| `capacidad_teu` | Simple |
| `anio_construccion` | Simple |
| `antiguedad` | **Derivado** de `anio_construccion` |

### 1.5 `CERTIFICADO`

| Atributo | Tipo |
|---|---|
| `numero_certificado` | Identificador |
| `fecha_expedicion` | Simple |
| `fecha_vencimiento` | Simple |

### 1.6 `OPERARIO`

| Atributo | Tipo |
|---|---|
| `id_operario` | Identificador |
| `nombre` | Simple |
| `licencia_portuaria` | Simple |

### 1.7 `SUMINISTRO`

| Atributo | Tipo |
|---|---|
| `codigo_suministro` | Identificador |
| `descripcion` | Simple |
| `unidad_medida` | Simple |

### 1.8 `CONTRATISTA`

| Atributo | Tipo |
|---|---|
| `nit` | Identificador |
| `razon_social` | Simple |
| `sede` | **Multivaluado y compuesto** → `ciudad`, `direccion` |

`sede` es el único atributo del modelo que combina ambas propiedades: se dibuja como elipse doble con dos elipses hijas.

### 1.9 `BODEGA`

| Atributo | Tipo |
|---|---|
| `codigo_bodega` | Identificador |
| `zona` | Simple |
| `capacidad_m2` | Simple |

### 1.10 Subclases de `OPERARIO`

| Subclase | Atributos propios |
|---|---|
| `GRUERO` | `horas_operacion`, `tipo_certificacion` |
| `ESTIBADOR` | `equipos_manejados` (**multivaluado**), `examen_medico_vigente` |

---

## 2. Relaciones

| # | Relación | Entidades | Card. | Participación | Atributos |
|---|---|---|---|---|---|
| 1 | **IDENTIFICA** *(identificadora)* | `SERVICIO` — `RECALADA` | 1:N | RECALADA total | — |
| 2 | **ATIENDE** | `BUQUE` — `RECALADA` | 1:N | RECALADA total, BUQUE parcial | — |
| 3 | **CLASIFICA** | `BUQUE` — `CERTIFICADO` | 1:1 | Ambos total | — |
| 4 | **EMBARQUE** | `IMPORTADOR` — `RECALADA` | M:N | — | `tipo_carga`, `peso_toneladas`, `fecha_solicitud` |
| 5 | **CUADRILLA** | `OPERARIO` — `RECALADA` | M:N | — | `turno`, `horas_trabajadas` |
| 6 | **SUPERVISA** *(recursiva)* | `OPERARIO` — `OPERARIO` | M:N | — | `periodo` |
| 7 | **TRANSBORDA** *(recursiva)* | `RECALADA` — `RECALADA` | 1:N | Parcial en ambos extremos | — |
| 8 | **ABASTECE** *(ternaria)* | `CONTRATISTA` — `RECALADA` — `SUMINISTRO` | M:N:N | — | `cantidad` |
| 9 | **DEPOSITA** *(sobre agregación)* | agregación `EMBARQUE` — `BODEGA` | N:1 | — | — |

### 2.1 Relaciones recursivas

**SUPERVISA** conecta `OPERARIO` consigo misma con dos roles distintos:

- `supervisor` — el operario que ejerce la supervisión
- `supervisado` — el operario supervisado

Es M:N porque un operario puede ser supervisado por distintos operarios en periodos distintos, y el atributo `periodo` de la relación es el que permite que esa historia no se pierda.

**TRANSBORDA** conecta `RECALADA` consigo misma:

- `recalada_origen` — la escala desde la que sale la carga
- `recalada_destino` — la escala a la que llega

Es 1:N con participación parcial en ambos extremos: la mayoría de recaladas no participan en un transbordo.

### 2.2 Relación ternaria

**ABASTECE** no puede descomponerse en tres relaciones binarias sin perder información: el hecho relevante es que *un contratista específico entregó una cantidad de un suministro específico en una recalada específica*. La cardinalidad **M:N:N** refleja que un mismo suministro puede ser entregado por varios contratistas en una misma recalada, y que cada contratista atiende muchas recaladas y muchos suministros.

El atributo `cantidad` pertenece a la relación, no a ninguna de las tres entidades.

### 2.3 Agregación

La relación **EMBARQUE** entre `IMPORTADOR` y `RECALADA` se trata como una entidad de nivel superior, encerrándola junto con sus dos entidades en el rectángulo de agregación. Sobre ese conjunto se define **DEPOSITA** hacia `BODEGA`.

La agregación es necesaria porque lo que se almacena en una bodega no es un importador ni una recalada por separado, sino **la participación concreta de un importador en una recalada**: el embarque. Cada embarque se almacena en una sola bodega, y una bodega recibe muchos embarques a lo largo del tiempo.

### 2.4 Especialización

`OPERARIO` se especializa en `GRUERO` y `ESTIBADOR`.

| Propiedad | Valor | Cómo se dibuja |
|---|---|---|
| Completitud | **Parcial** | Línea **simple** entre `OPERARIO` y el círculo |
| Solapamiento | **Solapada** | Letra **`o`** dentro del círculo |

Parcial porque hay operarios que no son ni grueros ni estibadores. Solapada porque un mismo operario puede ser las dos cosas a la vez.

Las líneas del círculo a cada subclase llevan el símbolo **⊂** apuntando hacia la superclase.

---

## 3. Resumen de construcciones EER presentes

| Construcción | Dónde aparece |
|---|---|
| Entidad débil y relación identificadora | `RECALADA` / **IDENTIFICA** |
| Atributo compuesto | `nombre_completo`, `sede` |
| Atributo multivaluado | `correo`, `sede`, `equipos_manejados` |
| Atributo multivaluado **y** compuesto | `sede` |
| Atributo derivado | `antiguedad_cliente`, `antiguedad` |
| Relación 1:1 | **CLASIFICA** |
| Relación 1:N | **IDENTIFICA**, **ATIENDE**, **TRANSBORDA**, **DEPOSITA** |
| Relación M:N | **EMBARQUE**, **CUADRILLA**, **SUPERVISA** |
| Relación recursiva | **SUPERVISA**, **TRANSBORDA** |
| Relación ternaria | **ABASTECE** |
| Atributos de relación | **EMBARQUE**, **CUADRILLA**, **SUPERVISA**, **ABASTECE** |
| Agregación | **DEPOSITA** sobre **EMBARQUE** |
| Especialización parcial y solapada | `OPERARIO` → `GRUERO`, `ESTIBADOR` |

---

## 4. Supuestos adoptados

El enunciado no especifica la participación en algunas relaciones. Se dibujaron así:

| # | Relación | Supuesto |
|---|---|---|
| S-01 | **IDENTIFICA** | `SERVICIO` con participación **parcial**: un servicio puede estar registrado sin recaladas aún |
| S-02 | **EMBARQUE** | Participación **parcial** en ambos extremos |
| S-03 | **CUADRILLA** | Participación **parcial** en ambos extremos |
| S-04 | **SUPERVISA** | Participación **parcial** en ambos roles: no todo operario supervisa ni es supervisado |
| S-05 | **ABASTECE** | Participación **parcial** en las tres entidades: no toda recalada requiere suministros |
| S-06 | **DEPOSITA** | Participación **total** del lado de la agregación, porque todo embarque se almacena en una bodega; `BODEGA` **parcial**, porque puede estar vacía |

Si el enunciado exige otra lectura, basta cambiar el tipo de línea en el diagrama: línea simple para parcial, línea doble para total.
