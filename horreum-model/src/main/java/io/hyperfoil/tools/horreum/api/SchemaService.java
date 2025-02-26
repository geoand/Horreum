package io.hyperfoil.tools.horreum.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.ValidationMessage;

import io.hyperfoil.tools.horreum.entity.json.Label;
import io.hyperfoil.tools.horreum.entity.json.Schema;
import io.hyperfoil.tools.horreum.entity.json.Transformer;

@Path("api/schema")
public interface SchemaService {
   @GET
   @Path("{id}")
   @Produces(MediaType.APPLICATION_JSON)
   Schema getSchema(@PathParam("id") int id, @QueryParam("token") String token);

   @GET
   @Path("idByUri/{uri}")
   int idByUri(@PathParam("uri") String uri);

   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   Integer add(Schema schema);

   @GET
   List<Schema> list(@QueryParam("limit") Integer limit,
                     @QueryParam("page") Integer page,
                     @QueryParam("sort") String sort,
                     @QueryParam("direction") @DefaultValue("Ascending") SortDirection direction);


   @GET
   @Path("descriptors")
   @Produces(MediaType.APPLICATION_JSON)
   List<SchemaDescriptor> descriptors(@QueryParam("id") List<Integer> ids);

   @POST
   @Produces(MediaType.TEXT_PLAIN)
   @Path("{id}/resetToken")
   String resetToken(@PathParam("id") int id);

   @POST
   @Produces(MediaType.TEXT_PLAIN)
   @Path("{id}/dropToken")
   String dropToken(@PathParam("id") int id);

   @POST
   @Path("{id}/updateAccess")
   @Consumes(MediaType.TEXT_PLAIN) //is POST the correct verb for this method as we are not uploading a new artefact?
   // TODO: it would be nicer to use @FormParams but fetchival on client side doesn't support that
   void updateAccess(@PathParam("id") int id,
                     @Parameter(required = true) @QueryParam("owner") String owner,
                     @Parameter(required = true) @QueryParam("access") int access);

   @DELETE
   @Path("{id}")
   void delete(@PathParam("id") int id);

   @GET
   @Path("findUsages")
   @Produces(MediaType.APPLICATION_JSON)
   List<LabelLocation> findUsages(@Parameter(required = true) @QueryParam("label") String label);

   @GET
   @Path("{schemaId}/transformers")
   @Produces(MediaType.APPLICATION_JSON)
   List<Transformer> listTransformers(@PathParam("schemaId") int schemaId);

   @POST
   @Path("{schemaId}/transformers")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   int addOrUpdateTransformer(@PathParam("schemaId") int schemaId,
                              @RequestBody(required = true) Transformer transformer);

   @DELETE
   @Path("{schemaId}/transformers/{transformerId}")
   void deleteTransformer(@PathParam("schemaId") int schemaId, @PathParam("transformerId") int transformerId);

   @GET
   @Path("{schemaId}/labels")
   @Produces(MediaType.APPLICATION_JSON)
   List<Label> labels(@PathParam("schemaId") int schemaId);

   @POST
   @Path("{schemaId}/labels")
   @Consumes(MediaType.APPLICATION_JSON)
   Integer addOrUpdateLabel(@PathParam("schemaId") int schemaId, @RequestBody(required = true) Label label);

   @DELETE
   @Path("{schemaId}/labels/{labelId}")
   void deleteLabel(@PathParam("schemaId") int schemaId, @PathParam("labelId") int labelId);

   @GET
   @Path("allLabels")
   @Produces(MediaType.APPLICATION_JSON)
   Collection<LabelInfo> allLabels(@QueryParam("name") String name);

   @GET
   @Path("allTransformers")
   @Produces(MediaType.APPLICATION_JSON)
   List<TransformerInfo> allTransformers();

   @org.eclipse.microprofile.openapi.annotations.media.Schema(anyOf = {
         LabelInFingerprint.class, LabelInRule.class, LabelInReport.class, LabelInVariable.class, LabelInView.class
   })
   abstract class LabelLocation {
      public final String type;
      public int testId;
      public String testName;

      public LabelLocation(String type, int testId, String testName) {
         this.type = type;
         this.testId = testId;
         this.testName = testName;
      }
   }

   class LabelInFingerprint extends LabelLocation {
      public LabelInFingerprint(int testId, String testName) {
         super("FINGERPRINT", testId, testName);
      }
   }

   class LabelInRule extends LabelLocation {
      public int ruleId;
      public String ruleName;

      public LabelInRule(int testId, String testName, int ruleId, String ruleName) {
         super("MISSINGDATA_RULE", testId, testName);
         this.ruleId = ruleId;
         this.ruleName = ruleName;
      }
   }

   class LabelInVariable extends LabelLocation {
      public int variableId;
      public String variableName;

      public LabelInVariable(int testId, String testName, int variableId, String variableName) {
         super("VARIABLE", testId, testName);
         this.variableId = variableId;
         this.variableName = variableName;
      }
   }

   class LabelInView extends LabelLocation {
      public int viewId;
      public String viewName;
      public int componentId;
      public String header;

      public LabelInView(int testId, String testName, int viewId, String viewName, int componentId, String header) {
         super("VIEW", testId, testName);
         this.viewId = viewId;
         this.componentId = componentId;
         this.viewName = viewName;
         this.header = header;
      }
   }

   class LabelInReport extends LabelLocation {
      public int configId;
      public String title;
      public String where; // component, filter, category, series, label
      public String name; // only set for component

      public LabelInReport(int testId, String testName, int configId, String title, String where, String name) {
         super("REPORT", testId, testName);
         this.configId = configId;
         this.title = title;
         this.where = where;
         this.name = name;
      }
   }

   class TransformerInfo {
      @JsonProperty(required = true)
      public int schemaId;
      @NotNull
      public String schemaUri;
      @NotNull
      public String schemaName;
      @JsonProperty(required = true)
      public int transformerId;
      @NotNull
      public String transformerName;
   }

   class SchemaDescriptor {
      @JsonProperty(required = true)
      public int id;
      @NotNull
      public String name;
      @NotNull
      public String uri;

      public SchemaDescriptor() {}

      public SchemaDescriptor(int id, String name, String uri) {
         this.id = id;
         this.name = name;
         this.uri = uri;
      }
   }

   class LabelInfo {
      @NotNull
      public String name;
      @JsonProperty(required = true)
      public boolean metrics;
      @JsonProperty(required = true)
      public boolean filtering;
      @NotNull
      public List<SchemaDescriptor> schemas = new ArrayList<>();

      public LabelInfo(String name) {
         this.name = name;
      }
   }
}
