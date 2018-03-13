package ch.hsr.ifs.cdttesting.cdttest.formatting;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.model.ICProject;


public class FormatterLoader {

   public static Map<String, String> formatterOptions = new HashMap<>(200);
   static {
      formatterOptions.put("org.eclipse.cdt.core.formatter.alignment_for_arguments_in_method_invocation", "16");
      formatterOptions.put("org.eclipse.cdt.core.formatter.alignment_for_assignment", "16");
      formatterOptions.put("org.eclipse.cdt.core.formatter.alignment_for_base_clause_in_type_declaration", "80");
      formatterOptions.put("org.eclipse.cdt.core.formatter.alignment_for_binary_expression", "16");
      formatterOptions.put("org.eclipse.cdt.core.formatter.alignment_for_compact_if", "16");
      formatterOptions.put("org.eclipse.cdt.core.formatter.alignment_for_conditional_expression", "34");
      formatterOptions.put("org.eclipse.cdt.core.formatter.alignment_for_conditional_expression_chain", "18");
      formatterOptions.put("org.eclipse.cdt.core.formatter.alignment_for_constructor_initializer_list", "0");
      formatterOptions.put("org.eclipse.cdt.core.formatter.alignment_for_declarator_list", "16");
      formatterOptions.put("org.eclipse.cdt.core.formatter.alignment_for_enumerator_list", "48");
      formatterOptions.put("org.eclipse.cdt.core.formatter.alignment_for_expression_list", "0");
      formatterOptions.put("org.eclipse.cdt.core.formatter.alignment_for_expressions_in_array_initializer", "16");
      formatterOptions.put("org.eclipse.cdt.core.formatter.alignment_for_member_access", "0");
      formatterOptions.put("org.eclipse.cdt.core.formatter.alignment_for_overloaded_left_shift_chain", "16");
      formatterOptions.put("org.eclipse.cdt.core.formatter.alignment_for_parameters_in_method_declaration", "16");
      formatterOptions.put("org.eclipse.cdt.core.formatter.alignment_for_throws_clause_in_method_declaration", "16");
      formatterOptions.put("org.eclipse.cdt.core.formatter.brace_position_for_array_initializer", "end_of_line");
      formatterOptions.put("org.eclipse.cdt.core.formatter.brace_position_for_block", "end_of_line");
      formatterOptions.put("org.eclipse.cdt.core.formatter.brace_position_for_block_in_case", "end_of_line");
      formatterOptions.put("org.eclipse.cdt.core.formatter.brace_position_for_method_declaration", "end_of_line");
      formatterOptions.put("org.eclipse.cdt.core.formatter.brace_position_for_namespace_declaration", "end_of_line");
      formatterOptions.put("org.eclipse.cdt.core.formatter.brace_position_for_switch", "end_of_line");
      formatterOptions.put("org.eclipse.cdt.core.formatter.brace_position_for_type_declaration", "end_of_line");
      formatterOptions.put("org.eclipse.cdt.core.formatter.comment.min_distance_between_code_and_line_comment", "1");
      formatterOptions.put("org.eclipse.cdt.core.formatter.comment.preserve_white_space_between_code_and_line_comments", "false");
      formatterOptions.put("org.eclipse.cdt.core.formatter.compact_else_if", "true");
      formatterOptions.put("org.eclipse.cdt.core.formatter.continuation_indentation", "2");
      formatterOptions.put("org.eclipse.cdt.core.formatter.continuation_indentation_for_array_initializer", "2");
      formatterOptions.put("org.eclipse.cdt.core.formatter.format_guardian_clause_on_one_line", "false");
      formatterOptions.put("org.eclipse.cdt.core.formatter.indent_access_specifier_compare_to_type_header", "false");
      formatterOptions.put("org.eclipse.cdt.core.formatter.indent_access_specifier_extra_spaces", "0");
      formatterOptions.put("org.eclipse.cdt.core.formatter.indent_body_declarations_compare_to_access_specifier", "true");
      formatterOptions.put("org.eclipse.cdt.core.formatter.indent_body_declarations_compare_to_namespace_header", "false");
      formatterOptions.put("org.eclipse.cdt.core.formatter.indent_breaks_compare_to_cases", "true");
      formatterOptions.put("org.eclipse.cdt.core.formatter.indent_declaration_compare_to_template_header", "false");
      formatterOptions.put("org.eclipse.cdt.core.formatter.indent_empty_lines", "false");
      formatterOptions.put("org.eclipse.cdt.core.formatter.indent_statements_compare_to_block", "true");
      formatterOptions.put("org.eclipse.cdt.core.formatter.indent_statements_compare_to_body", "true");
      formatterOptions.put("org.eclipse.cdt.core.formatter.indent_switchstatements_compare_to_cases", "true");
      formatterOptions.put("org.eclipse.cdt.core.formatter.indent_switchstatements_compare_to_switch", "false");
      formatterOptions.put("org.eclipse.cdt.core.formatter.indentation.size", "3");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_new_line_after_opening_brace_in_array_initializer", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_new_line_after_template_declaration", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_new_line_at_end_of_file_if_missing", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_new_line_before_catch_in_try_statement", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_new_line_before_closing_brace_in_array_initializer", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_new_line_before_colon_in_constructor_initializer_list", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_new_line_before_else_in_if_statement", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_new_line_before_identifier_in_function_declaration", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_new_line_before_while_in_do_statement", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_new_line_in_empty_block", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_assignment_operator", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_binary_operator", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_closing_angle_bracket_in_template_arguments", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_closing_angle_bracket_in_template_parameters", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_closing_brace_in_block", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_closing_paren_in_cast", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_colon_in_base_clause", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_colon_in_case", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_colon_in_conditional", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_colon_in_labeled_statement", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_array_initializer", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_base_types", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_declarator_list", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_enum_declarations", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_expression_list", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_method_declaration_parameters", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_method_declaration_throws", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_method_invocation_arguments", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_template_arguments", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_comma_in_template_parameters", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_angle_bracket_in_template_arguments", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_angle_bracket_in_template_parameters", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_brace_in_array_initializer", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_bracket", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_cast", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_catch", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_exception_specification", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_for", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_if", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_method_declaration", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_method_invocation", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_parenthesized_expression", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_switch", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_opening_paren_in_while", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_postfix_operator", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_prefix_operator", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_question_in_conditional", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_semicolon_in_for", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_after_unary_operator", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_assignment_operator", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_binary_operator", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_angle_bracket_in_template_arguments", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_angle_bracket_in_template_parameters", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_brace_in_array_initializer", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_bracket", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_cast", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_catch", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_exception_specification", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_for", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_if", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_method_declaration", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_method_invocation", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_parenthesized_expression", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_switch", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_closing_paren_in_while", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_colon_in_base_clause", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_colon_in_case", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_colon_in_conditional", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_colon_in_default", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_colon_in_labeled_statement", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_array_initializer", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_base_types", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_declarator_list", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_enum_declarations", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_expression_list", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_method_declaration_parameters", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_method_declaration_throws", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_method_invocation_arguments", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_template_arguments", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_comma_in_template_parameters", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_angle_bracket_in_template_arguments", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_angle_bracket_in_template_parameters", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_brace_in_array_initializer", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_brace_in_block", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_brace_in_method_declaration", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_brace_in_namespace_declaration", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_brace_in_switch", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_brace_in_type_declaration", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_bracket", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_catch", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_exception_specification", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_for", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_if", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_method_declaration", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_method_invocation", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_parenthesized_expression", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_switch", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_opening_paren_in_while", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_postfix_operator", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_prefix_operator", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_question_in_conditional", "insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_semicolon", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_semicolon_in_for", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_before_unary_operator", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_between_empty_braces_in_array_initializer", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_between_empty_brackets", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_between_empty_parens_in_exception_specification", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_between_empty_parens_in_method_declaration", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.insert_space_between_empty_parens_in_method_invocation", "do not insert");
      formatterOptions.put("org.eclipse.cdt.core.formatter.join_wrapped_lines", "true");
      formatterOptions.put("org.eclipse.cdt.core.formatter.keep_else_statement_on_same_line", "false");
      formatterOptions.put("org.eclipse.cdt.core.formatter.keep_empty_array_initializer_on_one_line", "false");
      formatterOptions.put("org.eclipse.cdt.core.formatter.keep_imple_if_on_one_line", "false");
      formatterOptions.put("org.eclipse.cdt.core.formatter.keep_then_statement_on_same_line", "false");
      formatterOptions.put("org.eclipse.cdt.core.formatter.lineSplit", "80");
      formatterOptions.put("org.eclipse.cdt.core.formatter.number_of_empty_lines_to_preserve", "1");
      formatterOptions.put("org.eclipse.cdt.core.formatter.put_empty_statement_on_new_line", "true");
      formatterOptions.put("org.eclipse.cdt.core.formatter.tabulation.char", "space");
      formatterOptions.put("org.eclipse.cdt.core.formatter.tabulation.size", "3");
      formatterOptions.put("org.eclipse.cdt.core.formatter.use_tabs_only_for_leading_indentations", "true");
   }

   public static void loadFormatter(ICProject project) {
      project.setOptions(formatterOptions);
   }
}
