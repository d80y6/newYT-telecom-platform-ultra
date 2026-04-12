{{/*
Expand the name of the chart.
*/}}
{{- define "kafka-kraft.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "kafka-kraft.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version.
*/}}
{{- define "kafka-kraft.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "kafka-kraft.labels" -}}
app.kubernetes.io/name: {{ include "kafka-kraft.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Create the name of the service account
*/}}
{{- define "kafka-kraft.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "kafka-kraft.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Generate KRaft cluster ID
*/}}
{{- define "kafka-kraft.clusterId" -}}
{{- if .Values.kraft.existingClusterId }}
{{- .Values.kraft.existingClusterId }}
{{- else }}
{{- default "KRaftCluster01" .Values.kraft.clusterId }}
{{- end }}
{{- end }}
