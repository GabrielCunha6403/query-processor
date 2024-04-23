package org.jarreto.service;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;


@ApplicationScoped
public class QueryProcessorService {


    @Transactional
    public Boolean queryProcessor(String query) {
        return validateQuery(query);
    }

    public boolean validateQueryOld(String query) {
        String regex = "^(select .* from \\S+(?: join \\S+ on \\S+ = \\S+)+)? where (\\S+ (?:=|>|<|>=|<=|<>) \\S+)+$";
        String regexWhere = "^select\\s+(\\*|.*\\S+)\\s+from\\s+\\w+\\s+(?:where\\s+.*(?:\\s+and\\s+.*\\s*)*)?;$";
        return query.toLowerCase().matches(regex) || query.toLowerCase().matches(regexWhere);
    }

    public boolean validateQuery(String query) {
        // Remove comentários (opcional)
        query = query.replaceAll("--.*", "");

        // Valida separadores básicos
        if (!query.toLowerCase().matches("^\\s*(select|with).*")) {
            return false;
        }

        // Valida cláusulas principais (SELECT, FROM, WHERE, JOIN)
        String[] clauses = query.split("\\s+(?i)(SELECT|FROM|WHERE|JOIN|ORDER\\s+BY|GROUP\\s+BY|HAVING)");
        if (clauses.length < 2 || !clauses[0].equalsIgnoreCase("select")) {
            return false;
        }

        // Valida cláusula SELECT (opcionalmente verificar colunas válidas)
        if (!validateSelectClause(clauses[1])) {
            return false;
        }

        // Valida cláusula FROM (check table names later in parser)
        if (!clauses[2].matches("^\\s*from\\s+\\w+.*")) {
            return false;
        }

        // Valida cláusulas WHERE e JOIN (check operadores e colunas mais tarde)
        for (int i = 3; i < clauses.length; i++) {
            String clause = clauses[i].toLowerCase();
            if (!clause.startsWith("where") && !clause.startsWith("join")) {
                return false;
            }
        }

        // Overall validation passed, further parsing can be done
        return true;
    }

    private boolean validateSelectClause(String selectClause) {
        // Implement logic to check for valid columns or expressions (optional)
        return !selectClause.isBlank(); // Basic check for non-empty select clause
    }

}
