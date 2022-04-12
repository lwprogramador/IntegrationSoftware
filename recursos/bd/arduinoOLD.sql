--
-- PostgreSQL database dump
--

-- Dumped from database version 12.10
-- Dumped by pg_dump version 12.10

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: entidades; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA entidades;


ALTER SCHEMA entidades OWNER TO postgres;

--
-- Name: herramientas; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA herramientas;


ALTER SCHEMA herramientas OWNER TO postgres;

--
-- Name: sesiones; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA sesiones;


ALTER SCHEMA sesiones OWNER TO postgres;

--
-- Name: fcn_guardar_operador(character varying, character varying, character varying, character varying, character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.fcn_guardar_operador(_nombre character varying, _apellidos character varying, _documento character varying, _usuario character varying, _clave character varying, _codigo character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $_$
DECLARE	
	dataResponse varchar;
	datosTablaResponse varchar;
	existeUsuario integer;
	existeOperador integer;	

BEGIN
	existeOperador:= (SELECT id FROM entidades.tbl_operador WHERE _unico = ($6));
	existeUsuario:= (SELECT COUNT(*) FROM sesiones.tbl_usuario WHERE UPPER(tx_usuario) = UPPER($4));

	IF(COALESCE(existeOperador, 0) > 0) THEN
		UPDATE entidades.tbl_operador SET tx_nombre = UPPER($1), tx_apellido = UPPER($2), tx_documento = UPPER($3)
		WHERE _unico = $6 RETURNING id INTO existeOperador;
		IF(COALESCE($5, '') <> '' )THEN
			UPDATE sesiones.tbl_usuario SET tx_clave = (SELECT MD5($5)) WHERE idusuario = existeOperador;		
		END IF;
	
		SELECT jsonb_agg(jsoni.*)::TEXT 
		INTO datosTablaResponse
		FROM(SELECT * FROM vst_operadores) AS jsoni;
	
		dataResponse:= (SELECT row_to_json(_json.*)
						FROM (SELECT
							FALSE AS status,
							0 AS cod_accion,
							datosTablaResponse AS contenido) AS _json);
	END IF;
/*	IF(existeUsuario > 0) THEN
		dataResponse:= (SELECT row_to_json(_json.*)
						FROM (SELECT
							FALSE AS status,
							-2 AS cod_accion,
							'[]' AS contenido) AS _json);
	END IF;*/

	IF(COALESCE(existeOperador, 0) = 0 AND existeUsuario = 0) THEN 
		INSERT INTO entidades.tbl_operador(_unico, tx_nombre, tx_apellido, tx_documento) 
					VALUES((CONCAT('OP-', (SELECT COUNT(*) FROM entidades.tbl_operador))), UPPER($1), UPPER($2), UPPER($3)) RETURNING id INTO existeOperador;
		INSERT INTO sesiones.tbl_usuario(_unico, idusuario, tx_usuario, tx_clave)
						VALUES((CONCAT('USR-', (SELECT COUNT(*) FROM sesiones.tbl_usuario))), existeOperador, UPPER($4), (SELECT MD5($5)));
		SELECT jsonb_agg(jsoni.*)::TEXT 
		INTO datosTablaResponse
		FROM(SELECT * FROM vst_operadores) AS jsoni;
	
		dataResponse:= (SELECT row_to_json(_json.*)
						FROM (SELECT
							FALSE AS status,
							0 AS cod_accion,
							datosTablaResponse AS contenido) AS _json);
	END IF;
	RETURN dataResponse;
END;
$_$;


ALTER FUNCTION public.fcn_guardar_operador(_nombre character varying, _apellidos character varying, _documento character varying, _usuario character varying, _clave character varying, _codigo character varying) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: tbl_operador; Type: TABLE; Schema: entidades; Owner: postgres
--

CREATE TABLE entidades.tbl_operador (
    id integer NOT NULL,
    _unico character varying(100) NOT NULL,
    bo_activo boolean DEFAULT true NOT NULL,
    tx_nombre character varying(100) NOT NULL,
    tx_apellido character varying(100) NOT NULL,
    tx_documento character varying(100) NOT NULL
);


ALTER TABLE entidades.tbl_operador OWNER TO postgres;

--
-- Name: tbl_operador_id_seq; Type: SEQUENCE; Schema: entidades; Owner: postgres
--

CREATE SEQUENCE entidades.tbl_operador_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE entidades.tbl_operador_id_seq OWNER TO postgres;

--
-- Name: tbl_operador_id_seq; Type: SEQUENCE OWNED BY; Schema: entidades; Owner: postgres
--

ALTER SEQUENCE entidades.tbl_operador_id_seq OWNED BY entidades.tbl_operador.id;


--
-- Name: tbl_aprietes_logs; Type: TABLE; Schema: herramientas; Owner: postgres
--

CREATE TABLE herramientas.tbl_aprietes_logs (
    id integer NOT NULL,
    fecha timestamp with time zone DEFAULT now() NOT NULL,
    idoperador integer NOT NULL,
    idherramienta integer NOT NULL,
    odt character varying(100) NOT NULL,
    cliente character varying(100) NOT NULL
);


ALTER TABLE herramientas.tbl_aprietes_logs OWNER TO postgres;

--
-- Name: tbl_aprietes_logs_detalle; Type: TABLE; Schema: herramientas; Owner: postgres
--

CREATE TABLE herramientas.tbl_aprietes_logs_detalle (
    id integer NOT NULL,
    idaprietelog integer NOT NULL,
    fecha timestamp with time zone DEFAULT now() NOT NULL,
    voltaje numeric(15,5) NOT NULL,
    corriente numeric(15,5) NOT NULL,
    presion numeric(15,5) NOT NULL,
    fuerza numeric(15,5) NOT NULL
);


ALTER TABLE herramientas.tbl_aprietes_logs_detalle OWNER TO postgres;

--
-- Name: tbl_aprietes_logs_detalle_id_seq; Type: SEQUENCE; Schema: herramientas; Owner: postgres
--

CREATE SEQUENCE herramientas.tbl_aprietes_logs_detalle_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE herramientas.tbl_aprietes_logs_detalle_id_seq OWNER TO postgres;

--
-- Name: tbl_aprietes_logs_detalle_id_seq; Type: SEQUENCE OWNED BY; Schema: herramientas; Owner: postgres
--

ALTER SEQUENCE herramientas.tbl_aprietes_logs_detalle_id_seq OWNED BY herramientas.tbl_aprietes_logs_detalle.id;


--
-- Name: tbl_aprietes_logs_id_seq; Type: SEQUENCE; Schema: herramientas; Owner: postgres
--

CREATE SEQUENCE herramientas.tbl_aprietes_logs_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE herramientas.tbl_aprietes_logs_id_seq OWNER TO postgres;

--
-- Name: tbl_aprietes_logs_id_seq; Type: SEQUENCE OWNED BY; Schema: herramientas; Owner: postgres
--

ALTER SEQUENCE herramientas.tbl_aprietes_logs_id_seq OWNED BY herramientas.tbl_aprietes_logs.id;


--
-- Name: tbl_herramientas; Type: TABLE; Schema: herramientas; Owner: postgres
--

CREATE TABLE herramientas.tbl_herramientas (
    id integer NOT NULL,
    _unico character varying(100) NOT NULL,
    bo_activo boolean DEFAULT true NOT NULL,
    tx_nombre character varying(100) NOT NULL,
    tx_serial character varying(100) NOT NULL,
    tx_medida character varying(100) NOT NULL,
    tx_diasfuera character varying(100) NOT NULL,
    tx_district character varying(100) NOT NULL
);


ALTER TABLE herramientas.tbl_herramientas OWNER TO postgres;

--
-- Name: tbl_herramientas_aprietes; Type: TABLE; Schema: herramientas; Owner: postgres
--

CREATE TABLE herramientas.tbl_herramientas_aprietes (
    id integer NOT NULL,
    _unico character varying(100) NOT NULL,
    idherramienta integer NOT NULL,
    bo_activo boolean DEFAULT true NOT NULL,
    bo_alta boolean DEFAULT true NOT NULL,
    tx_apriete character varying(100) NOT NULL,
    apriete numeric(15,5) NOT NULL,
    porc_emp numeric(15,5) NOT NULL,
    medida_maxp numeric(15,5) NOT NULL,
    medida_minp numeric(15,5) NOT NULL
);


ALTER TABLE herramientas.tbl_herramientas_aprietes OWNER TO postgres;

--
-- Name: tbl_herramientas_aprietes_id_seq; Type: SEQUENCE; Schema: herramientas; Owner: postgres
--

CREATE SEQUENCE herramientas.tbl_herramientas_aprietes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE herramientas.tbl_herramientas_aprietes_id_seq OWNER TO postgres;

--
-- Name: tbl_herramientas_aprietes_id_seq; Type: SEQUENCE OWNED BY; Schema: herramientas; Owner: postgres
--

ALTER SEQUENCE herramientas.tbl_herramientas_aprietes_id_seq OWNED BY herramientas.tbl_herramientas_aprietes.id;


--
-- Name: tbl_herramientas_id_seq; Type: SEQUENCE; Schema: herramientas; Owner: postgres
--

CREATE SEQUENCE herramientas.tbl_herramientas_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE herramientas.tbl_herramientas_id_seq OWNER TO postgres;

--
-- Name: tbl_herramientas_id_seq; Type: SEQUENCE OWNED BY; Schema: herramientas; Owner: postgres
--

ALTER SEQUENCE herramientas.tbl_herramientas_id_seq OWNED BY herramientas.tbl_herramientas.id;


--
-- Name: vst_herramientas; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.vst_herramientas AS
 SELECT h._unico AS codigo,
    h.bo_activo AS activo,
    h.tx_nombre AS nombre,
    h.tx_serial AS nro_serial,
    h.tx_medida AS medida_herramienta,
    h.tx_diasfuera AS dias_fuera,
    h.tx_district AS district,
    ( SELECT count(*) AS cant_aprietes
           FROM herramientas.tbl_herramientas_aprietes iha
          WHERE (iha.idherramienta = h.id)) AS cant_aprietes,
    ( SELECT jsonb_agg(jsoni.*) AS aprietes
           FROM ( SELECT tbl_herramientas_aprietes.id AS codigo,
                    tbl_herramientas_aprietes.bo_alta AS alta,
                    tbl_herramientas_aprietes.tx_apriete,
                    tbl_herramientas_aprietes.apriete,
                    tbl_herramientas_aprietes.porc_emp AS por_emp,
                    tbl_herramientas_aprietes.medida_maxp AS emp_max,
                    tbl_herramientas_aprietes.medida_minp AS emp_min
                   FROM herramientas.tbl_herramientas_aprietes
                  WHERE (tbl_herramientas_aprietes.idherramienta = h.id)
                  ORDER BY tbl_herramientas_aprietes.tx_apriete) jsoni) AS aprietes
   FROM herramientas.tbl_herramientas h;


ALTER TABLE public.vst_herramientas OWNER TO postgres;

--
-- Name: tbl_usuario; Type: TABLE; Schema: sesiones; Owner: postgres
--

CREATE TABLE sesiones.tbl_usuario (
    id integer NOT NULL,
    _unico character varying(100) NOT NULL,
    bo_activo boolean DEFAULT true NOT NULL,
    idusuario integer NOT NULL,
    tx_usuario character varying(100) NOT NULL,
    tx_clave character varying(100) NOT NULL
);


ALTER TABLE sesiones.tbl_usuario OWNER TO postgres;

--
-- Name: vst_operadores; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.vst_operadores AS
 SELECT op._unico AS codigo,
    op.tx_nombre AS nombres,
    op.tx_apellido AS apellidos,
    op.tx_documento AS documento,
    usr.tx_usuario AS usuario,
    usr.tx_clave AS clave,
    op.bo_activo AS activo
   FROM (entidades.tbl_operador op
     JOIN sesiones.tbl_usuario usr ON ((usr.idusuario = op.id)));


ALTER TABLE public.vst_operadores OWNER TO postgres;

--
-- Name: tbl_usuario_id_seq; Type: SEQUENCE; Schema: sesiones; Owner: postgres
--

CREATE SEQUENCE sesiones.tbl_usuario_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE sesiones.tbl_usuario_id_seq OWNER TO postgres;

--
-- Name: tbl_usuario_id_seq; Type: SEQUENCE OWNED BY; Schema: sesiones; Owner: postgres
--

ALTER SEQUENCE sesiones.tbl_usuario_id_seq OWNED BY sesiones.tbl_usuario.id;


--
-- Name: tbl_operador id; Type: DEFAULT; Schema: entidades; Owner: postgres
--

ALTER TABLE ONLY entidades.tbl_operador ALTER COLUMN id SET DEFAULT nextval('entidades.tbl_operador_id_seq'::regclass);


--
-- Name: tbl_aprietes_logs id; Type: DEFAULT; Schema: herramientas; Owner: postgres
--

ALTER TABLE ONLY herramientas.tbl_aprietes_logs ALTER COLUMN id SET DEFAULT nextval('herramientas.tbl_aprietes_logs_id_seq'::regclass);


--
-- Name: tbl_aprietes_logs_detalle id; Type: DEFAULT; Schema: herramientas; Owner: postgres
--

ALTER TABLE ONLY herramientas.tbl_aprietes_logs_detalle ALTER COLUMN id SET DEFAULT nextval('herramientas.tbl_aprietes_logs_detalle_id_seq'::regclass);


--
-- Name: tbl_herramientas id; Type: DEFAULT; Schema: herramientas; Owner: postgres
--

ALTER TABLE ONLY herramientas.tbl_herramientas ALTER COLUMN id SET DEFAULT nextval('herramientas.tbl_herramientas_id_seq'::regclass);


--
-- Name: tbl_herramientas_aprietes id; Type: DEFAULT; Schema: herramientas; Owner: postgres
--

ALTER TABLE ONLY herramientas.tbl_herramientas_aprietes ALTER COLUMN id SET DEFAULT nextval('herramientas.tbl_herramientas_aprietes_id_seq'::regclass);


--
-- Name: tbl_usuario id; Type: DEFAULT; Schema: sesiones; Owner: postgres
--

ALTER TABLE ONLY sesiones.tbl_usuario ALTER COLUMN id SET DEFAULT nextval('sesiones.tbl_usuario_id_seq'::regclass);


--
-- Name: tbl_operador tbl_operador_pkey; Type: CONSTRAINT; Schema: entidades; Owner: postgres
--

ALTER TABLE ONLY entidades.tbl_operador
    ADD CONSTRAINT tbl_operador_pkey PRIMARY KEY (id);


--
-- Name: tbl_aprietes_logs_detalle tbl_aprietes_logs_detalle_pkey; Type: CONSTRAINT; Schema: herramientas; Owner: postgres
--

ALTER TABLE ONLY herramientas.tbl_aprietes_logs_detalle
    ADD CONSTRAINT tbl_aprietes_logs_detalle_pkey PRIMARY KEY (id);


--
-- Name: tbl_aprietes_logs tbl_aprietes_logs_pkey; Type: CONSTRAINT; Schema: herramientas; Owner: postgres
--

ALTER TABLE ONLY herramientas.tbl_aprietes_logs
    ADD CONSTRAINT tbl_aprietes_logs_pkey PRIMARY KEY (id);


--
-- Name: tbl_herramientas_aprietes tbl_herramientas_aprietes_pkey; Type: CONSTRAINT; Schema: herramientas; Owner: postgres
--

ALTER TABLE ONLY herramientas.tbl_herramientas_aprietes
    ADD CONSTRAINT tbl_herramientas_aprietes_pkey PRIMARY KEY (id);


--
-- Name: tbl_herramientas tbl_herramientas_pkey; Type: CONSTRAINT; Schema: herramientas; Owner: postgres
--

ALTER TABLE ONLY herramientas.tbl_herramientas
    ADD CONSTRAINT tbl_herramientas_pkey PRIMARY KEY (id);


--
-- Name: tbl_usuario tbl_usuario_pkey; Type: CONSTRAINT; Schema: sesiones; Owner: postgres
--

ALTER TABLE ONLY sesiones.tbl_usuario
    ADD CONSTRAINT tbl_usuario_pkey PRIMARY KEY (id);


--
-- Name: tbl_operador_id_idx; Type: INDEX; Schema: entidades; Owner: postgres
--

CREATE INDEX tbl_operador_id_idx ON entidades.tbl_operador USING btree (id);


--
-- Name: tbl_aprietes_logs_detalle_id_idx; Type: INDEX; Schema: herramientas; Owner: postgres
--

CREATE INDEX tbl_aprietes_logs_detalle_id_idx ON herramientas.tbl_aprietes_logs_detalle USING btree (id);


--
-- Name: tbl_aprietes_logs_id_idx; Type: INDEX; Schema: herramientas; Owner: postgres
--

CREATE INDEX tbl_aprietes_logs_id_idx ON herramientas.tbl_aprietes_logs USING btree (id);


--
-- Name: tbl_herramientas_aprietes_id_idx; Type: INDEX; Schema: herramientas; Owner: postgres
--

CREATE INDEX tbl_herramientas_aprietes_id_idx ON herramientas.tbl_herramientas_aprietes USING btree (id);


--
-- Name: tbl_herramientas_id_idx; Type: INDEX; Schema: herramientas; Owner: postgres
--

CREATE INDEX tbl_herramientas_id_idx ON herramientas.tbl_herramientas USING btree (id);


--
-- Name: tbl_usuario_id_idx; Type: INDEX; Schema: sesiones; Owner: postgres
--

CREATE INDEX tbl_usuario_id_idx ON sesiones.tbl_usuario USING btree (id);


--
-- PostgreSQL database dump complete
--

