import React from "react";

/**
 * Reusable Glass Form Component
 */
export const GlassForm = ({ children, className = "", onSubmit, ...props }) => {
  return (
    <form className={`form-glass ${className}`} onSubmit={onSubmit} {...props}>
      {children}
    </form>
  );
};

/**
 * Glass Form Group (for organizing form fields)
 */
export const FormGroup = ({
  label,
  children,
  className = "",
  error,
  required = false,
  ...props
}) => {
  return (
    <div className={`mb-4 ${className}`} {...props}>
      {label && (
        <label className="block mb-2 font-medium text-sm text-white">
          {label}
          {required && <span className="text-red-400 ml-1">*</span>}
        </label>
      )}
      {children}
      {error && <div className="mt-1 text-red-400 text-sm">{error}</div>}
    </div>
  );
};

/**
 * Glass Input Field
 */
export const GlassInput = ({
  label,
  error,
  required = false,
  className = "",
  ...props
}) => {
  return (
    <FormGroup label={label} error={error} required={required}>
      <input className={`form-control-glass w-full ${className}`} {...props} />
    </FormGroup>
  );
};

/**
 * Glass Select Field
 */
export const GlassSelect = ({
  label,
  options = [],
  error,
  required = false,
  className = "",
  ...props
}) => {
  return (
    <FormGroup label={label} error={error} required={required}>
      <select className={`form-control-glass w-full ${className}`} {...props}>
        <option value="">Select an option...</option>
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </FormGroup>
  );
};

/**
 * Glass Textarea Field
 */
export const GlassTextarea = ({
  label,
  error,
  required = false,
  className = "",
  rows = 4,
  ...props
}) => {
  return (
    <FormGroup label={label} error={error} required={required}>
      <textarea
        className={`form-control-glass w-full ${className}`}
        rows={rows}
        {...props}
      />
    </FormGroup>
  );
};

export default GlassForm;
